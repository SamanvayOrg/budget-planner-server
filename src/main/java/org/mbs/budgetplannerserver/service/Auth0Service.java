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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    // Roles are effectively static for the life of a tenant, so one lookup per name per
    // process is plenty. Cleared on restart, which is the only time a role change here
    // would realistically need picking up.
    private final Map<String, String> roleIdCache = new ConcurrentHashMap<>();

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
        String cached = roleIdCache.get(roleName);
        if (cached != null) {
            return cached;
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
        roleIdCache.put(roleName, roleId);
        return roleId;
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
