package org.mbs.budgetplannerserver.service;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class Auth0Service {
    public static final String REQ_KEY_PASSWORD = "password";
    public static final String REQ_KEY_CONNECTION = "connection";
    public static final String REQ_KEY_NAME = "name";
    public static final String REQ_KEY_EMAIL = "email";
    public static final String REQ_KEY_ROLES = "roles";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_BEARER = "Bearer ";
    @Value("${auth0-mgt.audience}")
    private String audience;
    @Value("${auth0-mgt.domain}")
    private String domain;
    @Value("${spring.security.oauth2.client.registration.auth0-mgt.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.auth0-mgt.client-secret}")
    private String clientSecret;
    private final RestTemplate restTemplate;
    private Duration clockSkew = Duration.ofSeconds(60);
    private Clock clock = Clock.systemUTC();
    private OAuth2AccessToken tokenCache;
    // Role ids are cached because they change rarely, but "rarely" is not "never" and the
    // original incident this code exists to prevent was a stale role id. A role deleted and
    // recreated in the Auth0 dashboard comes back with a new id; an entry that never expired
    // would keep the dead one until the next restart and fail every assignment in between —
    // the same failure as before, arriving by a different route. The entry is therefore
    // given a lifetime, and is dropped outright when Auth0 rejects it (see forgetRole).
    static final Duration ROLE_CACHE_TTL = Duration.ofMinutes(30);

    private final Map<String, CachedRoleId> roleIdCache = new ConcurrentHashMap<>();

    private static final class CachedRoleId {
        final String id;
        final Instant expiresAt;

        CachedRoleId(String id, Instant expiresAt) {
            this.id = id;
            this.expiresAt = expiresAt;
        }
    }

    public Auth0Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Object> createUser(UserContract userContract) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(REQ_KEY_EMAIL, userContract.getEmail());
        requestBody.put(REQ_KEY_NAME, userContract.getName());
        requestBody.put(REQ_KEY_CONNECTION, "Username-Password-Authentication");
        requestBody.put(REQ_KEY_PASSWORD, generatingRandomAlphabeticString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getRefreshedToken().getTokenValue());

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
        String url = String.format("%s/%s", domain, "api/v2/users");
        ResponseEntity<Object> result = restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
        return result;
    }

    // Resolves an Auth0 role id from its NAME, against whichever tenant this instance is
    // pointed at.
    //
    // Previously these ids were read from the auth_role table, seeded once by
    // V1.2__AlterLoginUser.sql with literal rol_... values seen in one tenant. Role ids are
    // tenant-specific, so that binds the app to a single Auth0 tenant: pointed anywhere else
    // (this project has both budget-planner and budget-planner-prod), every stored id 404s
    // and user creation fails with nothing in the code to indicate why. Asking Auth0 for the
    // id by name removes the coupling entirely — no seeding, no drift, same behaviour in
    // dev, staging and prod.
    public String getRoleIdByName(String roleName) {
        CachedRoleId cached = roleIdCache.get(roleName);
        if (cached != null && clock.instant().isBefore(cached.expiresAt)) {
            return cached.id;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getRefreshedToken().getTokenValue());

        String url = String.format("%s/api/v2/roles?name_filter=%s", domain,
                URLEncoder.encode(roleName, StandardCharsets.UTF_8));
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), List.class);

        List<Map<String, Object>> roles = response.getBody() == null ? List.of() : response.getBody();
        // name_filter is a substring match, so "Admin" also returns "SuperAdmin" — match the
        // name exactly or we would hand out the wrong privilege level.
        String roleId = roles.stream()
                .filter(role -> roleName.equals(role.get("name")))
                .map(role -> (String) role.get("id"))
                .findFirst()
                .orElseThrow(() -> new AuthorizationServiceException(String.format(
                        "Auth0 tenant %s has no role named '%s' — cannot assign it to a new user",
                        domain, roleName)));
        roleIdCache.put(roleName, new CachedRoleId(roleId, clock.instant().plus(ROLE_CACHE_TTL)));
        return roleId;
    }

    // Drop a cached id the moment Auth0 tells us it is wrong, rather than waiting out the
    // rest of its lifetime. A role that was recreated fails with 404 on the id we hold; the
    // next attempt should look it up again instead of repeating the same dead id.
    public void forgetRole(String roleName) {
        roleIdCache.remove(roleName);
    }

    public ResponseEntity<String> assignRole(User user, List<String> roles) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(REQ_KEY_ROLES, roles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getRefreshedToken().getTokenValue());

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
        String url = String.format("%s/%s/%s/%s", domain, "api/v2/users",
                user.getUserName(), "roles");

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return result;
    }

    // Auth0 adds roles rather than replacing them, so changing someone's role means
    // removing the old one explicitly. Without this a demotion would leave the previous
    // role in place and the user would keep the privileges it carries.
    public ResponseEntity<String> removeRole(User user, List<String> roles) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(REQ_KEY_ROLES, roles);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getRefreshedToken().getTokenValue());

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
        String url = String.format("%s/%s/%s/%s", domain, "api/v2/users",
                user.getUserName(), "roles");

        return restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);
    }

    // The Auth0 account registered against an address, or null if there is none. Used to
    // make sense of a "user already exists" conflict: the address may belong to a live user,
    // or to an account this application created and then lost track of, and only Auth0 can
    // say which.
    public Map<String, Object> findUserByEmail(String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getRefreshedToken().getTokenValue());

        // Percent-encoding the address does not work here: this endpoint validates the raw
        // query value without decoding it first, so an encoded "@" arrives as %40 and is
        // rejected as a malformed email. The URI is therefore built with the address intact
        // — "@" and "+" are both legal in a query string — and passed as a URI so that
        // RestTemplate does not treat it as a template and encode it again.
        URI uri = UriComponentsBuilder.fromHttpUrl(domain + "/api/v2/users-by-email")
                .queryParam("email", email)
                .build()
                .toUri();
        ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.GET,
                new HttpEntity<>(headers), List.class);

        List<Map<String, Object>> users = response.getBody() == null ? List.of() : response.getBody();
        return users.isEmpty() ? null : users.get(0);
    }

    // Every role currently held in Auth0, by id. Read before revoking rather than assuming
    // the stored role is the only one: a role assigned by hand in the Auth0 dashboard would
    // otherwise survive a deletion and keep granting whatever it carries.
    public List<String> roleIdsOf(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getRefreshedToken().getTokenValue());

        String url = String.format("%s/%s/%s/%s", domain, "api/v2/users",
                user.getUserName(), "roles");
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(headers), List.class);

        List<Map<String, Object>> roles = response.getBody() == null ? List.of() : response.getBody();
        return roles.stream().map(role -> (String) role.get("id")).collect(Collectors.toList());
    }

    public ResponseEntity<String> sendChangePasswordEmail(User user) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", clientId);
        requestBody.put("email", user.getEmail());
        requestBody.put("connection", "Username-Password-Authentication");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getRefreshedToken().getTokenValue());

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
        String url = String.format("%s/%s", domain, "dbconnections/change_password");

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return result;
    }


    private OAuth2AccessToken getRefreshedToken() {
        if (tokenCache == null || hasTokenExpired(tokenCache)) {
            tokenCache = getManagementApiToken();
        }

        return tokenCache;
    }

    private OAuth2AccessToken getManagementApiToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject requestBody = new JSONObject();
        requestBody.put("client_id", clientId);
        requestBody.put("client_secret", clientSecret);
        requestBody.put("audience", audience);
        requestBody.put("grant_type", "client_credentials");

        String url = String.format("%s/%s", domain, "oauth/token");
        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, Object> result = restTemplate.postForObject(url, request, HashMap.class);
        OAuth2AccessToken a2at = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, (String) result.get("access_token"),
                this.clock.instant(), this.clock.instant().plusSeconds((Integer) (result.get("expires_in")))
                .minus(1, ChronoUnit.HOURS));

        return a2at;
    }

    private boolean hasTokenExpired(OAuth2Token token) {
        return this.clock.instant().isAfter(token.getExpiresAt().minus(this.clockSkew));
    }

    private String generatingRandomAlphabeticString() {
        return new PasswordGenerator().generatePassword(12,
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1)
        );
    }
}
