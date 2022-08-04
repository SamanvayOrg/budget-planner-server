package org.mbs.budgetplannerserver.service;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.mapper.Auth0UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

@Service
public class Auth0Service {

    public static final String DEFAULT_PASSWORD = "password";
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

    public Auth0Service(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getManagementApiToken() {
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
        HashMap<String, String> result = restTemplate.postForObject(url, request, HashMap.class);

        return result.get("access_token");
    }

    public ResponseEntity<Object> createUser(UserContract userContract) {
        JSONObject requestBody = new JSONObject();
        requestBody.put(REQ_KEY_EMAIL, userContract.getEmail());
        requestBody.put(REQ_KEY_NAME, userContract.getName());
        requestBody.put(REQ_KEY_CONNECTION, "Username-Password-Authentication");
        requestBody.put(REQ_KEY_PASSWORD, DEFAULT_PASSWORD);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getManagementApiToken());

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
        headers.set(HEADER_AUTHORIZATION, HEADER_BEARER + getManagementApiToken());

        HttpEntity<String> request = new HttpEntity<String>(requestBody.toString(), headers);
        String url = String.format("%s/%s/%s/%s", domain, "api/v2/users",
                user.getUserName(), "roles");

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return result;
    }
}
