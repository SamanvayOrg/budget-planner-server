package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.mbs.budgetplannerserver.security.LocalAuthSupport;
import org.mbs.budgetplannerserver.service.UserService;
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
            // No "admin" here, deliberately. The SuperAdmin role in Auth0 carries read,
            // write and superAdmin — not admin — so granting it locally made this sign-in
            // more permissive than the real thing and hid the difference: a Super Admin can
            // reach the user-administration endpoints here and would be refused them in
            // production. They administer municipalities and their admins through the
            // superAdmin-guarded endpoints instead.
            "superadmin", new LocalAccount(LOCAL_PASSWORD, "local|superadmin", List.of("read", "write", "superAdmin"))
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

    // Mirrors what each Auth0 role actually grants, verified against the tenant. Deriving
    // this from the isAdmin flag instead would give a Read-only user write access locally,
    // so the local sign-in would be more permissive than production and hide exactly the
    // kind of bug it exists to catch. superAdmin is deliberately absent: the create-user
    // flow never grants it, so no locally created account should be able to obtain it.
    private List<String> permissionsFor(User user) {
        String role = user.getRole();
        if (UserService.ADMIN_USER_ROLE.equals(role)) {
            return List.of("read", "write", "admin");
        }
        if (UserService.READ_ONLY_ROLE.equals(role)) {
            return List.of("read");
        }
        return List.of("read", "write");
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
