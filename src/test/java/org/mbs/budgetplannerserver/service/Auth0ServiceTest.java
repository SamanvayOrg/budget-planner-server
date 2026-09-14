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

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    // The incident this whole lookup exists to prevent was a stale role id. Caching the id
    // for the life of the process reintroduces it by another route: a role deleted and
    // recreated in the Auth0 dashboard comes back with a new id, and an entry that never
    // expires keeps the dead one until the next restart.
    @Test
    public void shouldResolveTheRoleAgainOnceTheCachedIdHasExpired() {
        auth0ReturnsRoles(List.of(Map.of("id", "rol_FIRST", "name", "Admin")));
        assertEquals("rol_FIRST", auth0Service.getRoleIdByName("Admin"));

        // Within its lifetime the cached id is reused and Auth0 is not asked again.
        assertEquals("rol_FIRST", auth0Service.getRoleIdByName("Admin"));
        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(List.class));

        // Move past the lifetime; the role has meanwhile been recreated with a new id.
        ReflectionTestUtils.setField(auth0Service, "clock",
                Clock.fixed(Instant.now().plus(Auth0Service.ROLE_CACHE_TTL).plusSeconds(60), ZoneOffset.UTC));
        auth0ReturnsRoles(List.of(Map.of("id", "rol_SECOND", "name", "Admin")));

        assertEquals("rol_SECOND", auth0Service.getRoleIdByName("Admin"),
                "an expired entry must be looked up again, not served stale");
    }

    // A rejected assignment means the id we hold is wrong now, not in thirty minutes.
    @Test
    public void shouldDropACachedIdWhenItIsForgotten() {
        auth0ReturnsRoles(List.of(Map.of("id", "rol_FIRST", "name", "Admin")));
        assertEquals("rol_FIRST", auth0Service.getRoleIdByName("Admin"));

        auth0Service.forgetRole("Admin");
        auth0ReturnsRoles(List.of(Map.of("id", "rol_SECOND", "name", "Admin")));

        assertEquals("rol_SECOND", auth0Service.getRoleIdByName("Admin"));
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
