package org.mbs.budgetplannerserver.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    private final Environment environment;

    public SecurityConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        boolean isLocal = environment.acceptsProfiles(Profiles.of("local"));

        // CSRF protection guards against a browser silently attaching an ambient credential
        // (a session cookie) to a forged cross-site request. This API authenticates solely
        // from an explicit Authorization: Bearer header that no third-party site can cause
        // the browser to send, so CSRF adds no protection here — while its filter does
        // reject unauthenticated POSTs to otherwise-public endpoints with a bare 403, since
        // no client in this system ever fetches or sends a CSRF token.
        http.csrf().disable();

        // Belongs with the CSRF decision above: with CSRF off, an ambient session cookie
        // would become a usable credential for writes. Declaring the API stateless keeps
        // Spring from ever creating or reading an HttpSession, so the Bearer token stays
        // the only thing that authenticates a request.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorization =
                http.authorizeRequests();
        if (isLocal) {
            // LOCAL DEV ONLY: the local sign-in endpoint has to be reachable without a
            // token. Registered only under the `local` profile so production never carries
            // an unauthenticated allowlist entry for it.
            authorization.antMatchers("/api/local-auth/**").permitAll();
        }
        // Order matters: Spring evaluates these in registration order and stops at the
        // first match. /api/** must come before the /** catch-all, or every request —
        // including API calls — matches /** first and is permitted.
        authorization
                .antMatchers("/api/**").authenticated()
                .antMatchers("/**").permitAll();

        http.oauth2ResourceServer()
                .jwt()
                .decoder(isLocal ? localJwtDecoder() : jwtDecoder())
                .jwtAuthenticationConverter(jwtAuthenticationConverter());
    }

    // LOCAL DEV ONLY — trusts tokens minted by LocalAuthSupport/LocalAuthController
    // instead of validating against the real Auth0 tenant. Only reachable when the
    // `local` Spring profile is active.
    JwtDecoder localJwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(LocalAuthSupport.SECRET.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.PUT.name(),
                HttpMethod.POST.name(),
                HttpMethod.DELETE.name()
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration.applyPermitDefaultValues());
        return source;
    }

    JwtDecoder jwtDecoder() {
        OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(withAudience, withIssuer);

        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuer);
        jwtDecoder.setJwtValidator(validator);
        return jwtDecoder;
    }

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthoritiesClaimName("permissions");
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }
}