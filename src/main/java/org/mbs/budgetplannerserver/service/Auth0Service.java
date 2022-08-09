package org.mbs.budgetplannerserver.service;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class Auth0Service {
    public static final int leftLimit = 97; // letter 'a'
    public static final int rightLimit = 122; // letter 'z'
    public static final int targetStringLength = 10;
    public static final Random random = new Random();
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
        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
