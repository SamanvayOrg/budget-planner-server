package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.mbs.budgetplannerserver.security.LocalAuthSupport;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

// LOCAL DEV ONLY — active only under the `local` Spring profile. Lets QA log in against
// seeded users without a real Auth0 tenant. See LocalAuthSupport + SecurityConfig.
@RestController
@Profile("local")
public class LocalAuthController {

    static final String LOCAL_PASSWORD = "local1234";

    private static final Map<String, LocalAccount> ACCOUNTS = Map.of(
            "chiefofficer", new LocalAccount(LOCAL_PASSWORD, "local|chiefofficer", List.of("read", "write", "admin")),
            "accountant", new LocalAccount(LOCAL_PASSWORD, "local|accountant", List.of("read", "write")),
            "superadmin", new LocalAccount(LOCAL_PASSWORD, "local|superadmin", List.of("read", "write", "admin", "superAdmin"))
    );

    private final UserRepository userRepository;

    public LocalAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/api/local-auth/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        LocalAccount seeded = ACCOUNTS.get(username);
        if (seeded != null) {
            if (!seeded.password.equals(password)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad local-dev credentials");
            }
            return Map.of("token", LocalAuthSupport.mint(seeded.subject, seeded.permissions));
        }

        // Not one of the three seeded accounts. Fall back to any user that actually exists
        // in login_user — which is how a user created through the app (Chief Officer ->
        // create accountant) becomes reachable locally. Their real password lives in Auth0
        // and is unknown to us, so the shared local-dev password stands in for it. This is
        // only ever reachable under the `local` profile.
        User user = userRepository.findByEmail(username);
        if (user == null || !LOCAL_PASSWORD.equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad local-dev credentials");
        }
        return Map.of("token", LocalAuthSupport.mint(user.getUserName(), permissionsFor(user)));
    }

    // Mirrors the permissions the real Auth0 roles carry: RegularUser gets read+write,
    // Admin additionally gets admin. superAdmin is deliberately not derivable here — it is
    // never granted by the create-user flow, so no locally created account should get it.
    private List<String> permissionsFor(User user) {
        return Boolean.TRUE.equals(user.getAdmin())
                ? List.of("read", "write", "admin")
                : List.of("read", "write");
    }

    private static final class LocalAccount {
        final String password;
        final String subject;
        final List<String> permissions;

        LocalAccount(String password, String subject, List<String> permissions) {
            this.password = password;
            this.subject = subject;
            this.permissions = permissions;
        }
    }
}
