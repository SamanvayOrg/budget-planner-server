package org.mbs.budgetplannerserver.controller;

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
// seeded local users without a real Auth0 tenant. See LocalAuthSupport + SecurityConfig.
@RestController
@Profile("local")
public class LocalAuthController {

    private static final Map<String, LocalAccount> ACCOUNTS = Map.of(
            "chiefofficer", new LocalAccount("local1234", "local|chiefofficer", List.of("read", "write", "admin")),
            "accountant", new LocalAccount("local1234", "local|accountant", List.of("read", "write")),
            "superadmin", new LocalAccount("local1234", "local|superadmin", List.of("read", "write", "admin", "superAdmin"))
    );

    @PostMapping("/api/local-auth/login")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        LocalAccount account = ACCOUNTS.get(body.get("username"));
        if (account == null || !account.password.equals(body.get("password"))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad local-dev credentials");
        }
        return Map.of("token", LocalAuthSupport.mint(account.subject, account.permissions));
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
