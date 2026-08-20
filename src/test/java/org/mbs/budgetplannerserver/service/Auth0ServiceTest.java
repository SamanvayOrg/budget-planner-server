package org.mbs.budgetplannerserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Auth0ServiceTest {

    private RestTemplate restTemplate;
    private Auth0Service auth0Service;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        auth0Service = new Auth0Service(restTemplate);
        ReflectionTestUtils.setField(auth0Service, "domain", "https://tenant.us.auth0.com");
        // Pre-seed a live token so getRefreshedToken() doesn't try to reach Auth0.
        ReflectionTestUtils.setField(auth0Service, "tokenCache", new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "test-token",
                Instant.now(), Instant.now().plusSeconds(3600)));
    }

    private void auth0ReturnsRoles(List<Map<String, Object>> roles) {
        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(List.class)))
                .thenReturn(new ResponseEntity(roles, HttpStatus.OK));
    }

    // Auth0's name_filter is a SUBSTRING match, so asking for "Admin" also returns
    // "SuperAdmin". Picking the first result would hand a new user super-admin rights.
    @Test
    public void shouldMatchTheRoleNameExactlyRatherThanTakingTheFirstSubstringHit() {
        auth0ReturnsRoles(List.of(
                Map.of("id", "rol_SUPER", "name", "SuperAdmin"),
                Map.of("id", "rol_ADMIN", "name", "Admin")));

        assertThat(auth0Service.getRoleIdByName("Admin"), is("rol_ADMIN"));
    }

    // The point of the lookup: whatever id this tenant happens to use is what gets returned,
    // rather than a value baked into a migration that is only correct for one tenant.
    @Test
    public void shouldResolveWhicheverTenantItIsPointedAt() {
        auth0ReturnsRoles(List.of(Map.of("id", "rol_TENANT_SPECIFIC", "name", "RegularUser")));

        assertThat(auth0Service.getRoleIdByName("RegularUser"), is("rol_TENANT_SPECIFIC"));
    }

    // A missing role must fail loudly and name the tenant — the old failure mode was an
    // opaque 404 from Auth0 with nothing pointing at the real cause.
    @Test
    public void shouldFailWithAnActionableMessageWhenTheTenantHasNoSuchRole() {
        auth0ReturnsRoles(List.of(Map.of("id", "rol_SUPER", "name", "SuperAdmin")));

        AuthorizationServiceException thrown = assertThrows(AuthorizationServiceException.class,
                () -> auth0Service.getRoleIdByName("RegularUser"));

        assertThat(thrown.getMessage().contains("RegularUser"), is(true));
        assertThat(thrown.getMessage().contains("tenant.us.auth0.com"), is(true));
    }

    @Test
    public void shouldCacheTheLookupRatherThanCallingAuth0OnEveryUserCreation() {
        auth0ReturnsRoles(List.of(Map.of("id", "rol_ADMIN", "name", "Admin")));

        auth0Service.getRoleIdByName("Admin");
        auth0Service.getRoleIdByName("Admin");
        auth0Service.getRoleIdByName("Admin");

        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(List.class));
    }

    @Test
    public void shouldAskAuth0ForTheRoleByName() {
        auth0ReturnsRoles(List.of(Map.of("id", "rol_ADMIN", "name", "Admin")));

        auth0Service.getRoleIdByName("Admin");

        verify(restTemplate).exchange(contains("name_filter=Admin"), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(List.class));
    }
}
