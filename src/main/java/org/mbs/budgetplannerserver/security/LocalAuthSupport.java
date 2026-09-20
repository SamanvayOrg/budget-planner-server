package org.mbs.budgetplannerserver.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.time.Instant;
import java.util.Date;
import java.util.List;

// LOCAL DEV ONLY. Mints and verifies HS256 tokens under the `local` Spring profile so QA
// can log in against seeded users without a real Auth0 tenant. Never active unless
// `local` is in spring.profiles.active — see SecurityConfig.localJwtDecoder().
public final class LocalAuthSupport {
    public static final String SECRET = "local-dev-only-secret-never-use-outside-local-profile-32bytes-min";
    public static final String AUDIENCE = "https://api.budget-planner";
    public static final String ISSUER = "local-dev";

    private LocalAuthSupport() {
    }

    public static String mint(String subject, List<String> permissions) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .audience(AUDIENCE)
                    .issuer(ISSUER)
                    .claim("permissions", permissions)
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusSeconds(86400)))
                    .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(new MACSigner(SECRET.getBytes()));
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to mint local dev token", e);
        }
    }
}
