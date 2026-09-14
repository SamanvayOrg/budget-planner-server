package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static final String REGULAR_USER_ROLE = "RegularUser";
    public static final String ADMIN_USER_ROLE = "Admin";
    public static final String READ_ONLY_ROLE = "Read-only";

    // The roles an administrator may assign through the create-user screen. Admin is
    // deliberately absent: a Municipality Admin may not create another Admin, and a Super
    // Admin creates them through /api/municipality/{id}/adminUser instead. SuperAdmin is
    // absent for the same reason — nothing in the application grants it.
    public static final List<String> ASSIGNABLE_ROLES = List.of(REGULAR_USER_ROLE, READ_ONLY_ROLE);

    // Every role name the application understands, used to reject unknown values before
    // they reach Auth0 (where an unknown name would fail late, after the account exists).
    public static final List<String> KNOWN_ROLES = List.of(ADMIN_USER_ROLE, REGULAR_USER_ROLE, READ_ONLY_ROLE);

    private final MunicipalityService municipalityService;
    private final UserRepository userRepository;
    private final Auth0Service auth0Service;
    private final Environment environment;

    @Autowired
    // AuthRoleRepository is deliberately no longer a dependency: role ids now come from
    // Auth0 by name (Auth0Service#getRoleIdByName) rather than from the auth_role table,
    // whose seeded values are tenant-specific and silently wrong on any other tenant.
    public UserService(MunicipalityService municipalityService, UserRepository userRepository,
                       Auth0Service auth0Service, Environment environment) {
        this.municipalityService = municipalityService;
        this.userRepository = userRepository;
        this.auth0Service = auth0Service;
        this.environment = environment;
    }


    // A token can outlive the account it belongs to: deleting a user voids the row but any
    // token already issued stays valid until it expires, and an account whose creation
    // failed part way through never got a row at all. The lookup then returned null and
    // every caller dereferenced it, so the request died as a 500 with a stack trace.
    // Refusing it outright is both the correct answer and the one that closes the window
    // left by a token that is still technically valid.
    public User getUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "This account is no longer active for this application");
        }
        return user;
    }

    @Transactional
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Iterable<User> getAllMunicipalityAdmins() {
        return userRepository.findByIsAdmin(true);
    }

    // Goes through getUser rather than repeating the lookup, so a caller whose row is gone
    // gets the same clean refusal instead of a null dereference and a 500.
    @Transactional
    public Iterable<User> getAllUsers() {
        return userRepository.findByMunicipalityId(getUser().getMunicipality().getId());
    }

    @Transactional
    public Municipality getMunicipality() {
        return getUser().getMunicipality();
    }

    public User create(UserContract userContract) {
            // Resolve the role BEFORE creating anything in Auth0. Creating the Auth0 user is
            // an external, irreversible side effect and this method is not (and cannot
            // usefully be) transactional — so if the role lookup failed afterwards, the Auth0
            // account would already exist while no login_user row was ever written. The next
            // attempt with that same email then gets 409 "user already exists" from Auth0,
            // which the UI reports as "User already present" for someone who was never
            // actually created: the address is permanently unusable. Resolving first costs
            // nothing and leaves no orphan behind.
            String roleName = roleNameFor(userContract);
            String roleId = auth0Service.getRoleIdByName(roleName);

            ResponseEntity<Object> response = auth0Service.createUser(userContract);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AuthorizationServiceException("Unable to create user");
            }
            LinkedHashMap<String, Object> authRes = (LinkedHashMap) response.getBody();
            User user = new User();
            user.setUserName((String) authRes.get("user_id"));
            user.setEmail((String) authRes.get("email"));
            user.setName((String) authRes.get("name"));
            user.setRole(roleName);
            // Derived from the role actually assigned rather than taken from the request,
            // so the stored flag can never disagree with the role held in Auth0.
            user.setAdmin(ADMIN_USER_ROLE.equals(roleName));
            user.setMunicipality(municipalityService.getMunicipality(userContract.getMunicipalityId()));
            User savedUser = assignRolesAndSaveUser(roleName, roleId, user);
            sendPasswordSetupEmail(savedUser);
            return savedUser;
    }

    // Auth0#createUser sets a random password that is never shown to anyone — not to the
    // administrator creating the account, and not to the new user. Without this call the
    // account exists but nobody can sign in to it, and no message is ever sent, so the new
    // user has no way to know the account exists. Auth0's change-password mail doubles as
    // the invitation: it lets them set a password of their own.
    //
    // A failure here must not fail the request. The Auth0 account and the local row are
    // both already created and valid at this point; throwing would report failure for a
    // user that genuinely exists, and a retry would then hit "user already exists". It is
    // logged instead so the administrator can re-send from the user list.
    private void sendPasswordSetupEmail(User user) {
        try {
            auth0Service.sendChangePasswordEmail(user);
        } catch (Exception e) {
            logger.warn("User {} was created but the password-setup email could not be sent. "
                    + "They cannot sign in until it is re-sent or they use 'Forgot password'.",
                    user.getEmail(), e);
        }
    }

    // The role the contract is asking for. An explicit role wins over the isAdmin flag, so
    // that a caller cannot request Admin while passing isAdmin=false and slip past the
    // privilege check in UserController — which asks this same method what is being
    // requested. When no role is given the old flag-derived behaviour is kept, which is
    // what the Super Admin's create-an-admin endpoint relies on.
    public static String roleNameFor(UserContract userContract) {
        String requested = userContract.getRole();
        if (requested == null || requested.isBlank()) {
            return Boolean.TRUE.equals(userContract.getAdmin()) ? ADMIN_USER_ROLE : REGULAR_USER_ROLE;
        }
        if (!KNOWN_ROLES.contains(requested)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Unknown role '%s'. Expected one of %s", requested, KNOWN_ROLES));
        }
        return requested;
    }

    private User assignRolesAndSaveUser(String roleName, String roleId, User user) {
        ResponseEntity<String> response = auth0Service.assignRole(user, Arrays.asList(roleId));
        if(!response.getStatusCode().is2xxSuccessful()) {
            // The id we sent may be a cached one that Auth0 no longer recognises, which is
            // what happens when a role is deleted and recreated in the dashboard. Drop it so
            // the next attempt resolves the name afresh rather than repeating a dead id.
            auth0Service.forgetRole(roleName);
            throw new AuthorizationServiceException("Unable to assign roles to user");
        }
        return save(user);
    }

    public User sendChangePasswordEmail(User user) {
        auth0Service.sendChangePasswordEmail(user);
        return user;
    }

    // The role a request is asking an EXISTING user to become. This cannot simply reuse
    // roleNameFor: that derives the role from the isAdmin flag when none is given, and the
    // existing update screens send only the flag. A Read-only user edited for their name
    // alone would come back as RegularUser — a silent promotion nobody asked for. So the
    // current role is kept unless the request either names a role or actually changes the
    // admin flag.
    public static String roleNameForUpdate(UserContract userContract, User existingUser) {
        String requested = userContract.getRole();
        if (requested != null && !requested.isBlank()) {
            return roleNameFor(userContract);
        }
        boolean wasAdmin = Boolean.TRUE.equals(existingUser.getAdmin());
        boolean wantsAdmin = Boolean.TRUE.equals(userContract.getAdmin());
        if (wasAdmin == wantsAdmin) {
            return existingUser.getRole() == null
                    ? (wasAdmin ? ADMIN_USER_ROLE : REGULAR_USER_ROLE)
                    : existingUser.getRole();
        }
        return wantsAdmin ? ADMIN_USER_ROLE : REGULAR_USER_ROLE;
    }

    // Deliberately NOT @Transactional. Changing a role touches Auth0, which no database
    // rollback can undo — holding a transaction open across that call would only widen the
    // window in which the two can disagree. The repository methods carry their own
    // transactions.
    public User update(Long userId, UserContract userContract) {
        User user = getUser(userId);
        String currentRole = user.getRole();
        String newRole = roleNameForUpdate(userContract, user);

        if (!newRole.equals(currentRole)) {
            applyRoleChange(user, currentRole, newRole);
            user.setRole(newRole);
            // Kept in step with the role rather than taken from the request, so the flag
            // the admin list is built from cannot disagree with the role on the same row.
            user.setAdmin(ADMIN_USER_ROLE.equals(newRole));
        }
        user.setName(userContract.getName());
        return save(user);
    }

    // Auth0 holds the role that actually decides what a user may do; the database only
    // records it. Changing one without the other is how a demoted administrator keeps
    // administrator access, so the remote change happens first and a failure aborts the
    // whole update rather than leaving the two out of step.
    //
    // The old role is removed before the new one is added. The reverse order would leave a
    // user holding both if the removal failed — which for a demotion means keeping exactly
    // the privileges being taken away. Failing with no role is recoverable by retrying;
    // failing with too much privilege is a silent hole.
    private void applyRoleChange(User user, String currentRole, String newRole) {
        if (skipRemoteIdentityChanges()) {
            logger.info("Local profile: not changing {}'s role in Auth0. Local sign-in mints "
                    + "tokens from the stored role, so updating the database is the whole change.",
                    user.getEmail());
            return;
        }
        if (currentRole != null && !currentRole.isBlank()) {
            String currentRoleId = auth0Service.getRoleIdByName(currentRole);
            ResponseEntity<String> removed = auth0Service.removeRole(user, List.of(currentRoleId));
            if (!removed.getStatusCode().is2xxSuccessful()) {
                throw new AuthorizationServiceException(
                        "Unable to remove the existing role in Auth0; the user's privileges are unchanged");
            }
        }
        String newRoleId = auth0Service.getRoleIdByName(newRole);
        ResponseEntity<String> assigned = auth0Service.assignRole(user, List.of(newRoleId));
        if (!assigned.getStatusCode().is2xxSuccessful()) {
            throw new AuthorizationServiceException(String.format(
                    "Removed %s from %s in Auth0 but could not assign %s. They now have no role and "
                            + "cannot use the application until this is retried.",
                    currentRole, user.getEmail(), newRole));
        }
    }

    // Seeded local-development accounts exist only in the database — their user name is a
    // "local|..." marker, not an Auth0 user id — and the local sign-in derives permissions
    // from the stored role rather than from an Auth0 token. Under that profile the database
    // is the whole system of record, so there is nothing remote to keep in step.
    private boolean skipRemoteIdentityChanges() {
        return environment.acceptsProfiles(Profiles.of("local"));
    }


    // Deleting only marked the row voided. Auth0 kept the account and its roles, so the
    // person could still sign in and present a token carrying the privileges they had just
    // had taken away. Revoking in Auth0 is what actually ends their access.
    //
    // Not @Transactional, for the same reason as update: the Auth0 call cannot be rolled
    // back, so there is nothing to gain from holding a transaction across it.
    public User delete(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        revokeRemoteAccess(user);
        userRepository.delete(user);
        return user;
    }

    // Strips every role the account holds in Auth0. Once it has none, the permissions claim
    // in any token it is issued is empty and every endpoint refuses it — and getUser below
    // refuses the request anyway, because the row is voided.
    //
    // The identity itself is left in place. Auth0's own "block" would be the tidier match
    // for a soft delete, but it is a PATCH, and the HTTP client in use cannot issue one
    // (pinned by RestTemplateHttpMethodsTest). Removing the roles achieves the security
    // outcome with methods that do work. A consequence worth knowing: the address stays
    // registered in Auth0, so re-creating the same person still collides until creation is
    // made idempotent.
    private void revokeRemoteAccess(User user) {
        if (skipRemoteIdentityChanges()) {
            logger.info("Local profile: not revoking {} in Auth0. Local sign-in reads the database, "
                    + "and the row is about to be voided.", user.getEmail());
            return;
        }
        List<String> heldRoles = auth0Service.roleIdsOf(user);
        if (heldRoles.isEmpty()) {
            return;
        }
        ResponseEntity<String> response = auth0Service.removeRole(user, heldRoles);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new AuthorizationServiceException(String.format(
                    "Could not revoke %s's access in Auth0, so they have not been deleted. "
                            + "Deleting the record alone would leave them able to sign in.", user.getEmail()));
        }
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
