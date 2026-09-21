package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static final String REGULAR_USER_ROLE = "RegularUser";
    public static final String ADMIN_USER_ROLE = "Admin";
    public static final String READ_ONLY_ROLE = "Read-only";

    // Roles an admin may assign at creation. Admin is granted only via the Super Admin endpoint.
    public static final List<String> ASSIGNABLE_ROLES = List.of(REGULAR_USER_ROLE, READ_ONLY_ROLE);

    public static final List<String> KNOWN_ROLES = List.of(ADMIN_USER_ROLE, REGULAR_USER_ROLE, READ_ONLY_ROLE);

    private final MunicipalityService municipalityService;
    private final UserRepository userRepository;
    private final Auth0Service auth0Service;

    @Autowired
    public UserService(MunicipalityService municipalityService, UserRepository userRepository,
                       Auth0Service auth0Service) {
        this.municipalityService = municipalityService;
        this.userRepository = userRepository;
        this.auth0Service = auth0Service;
    }


    // A valid token may belong to an account with no row (deleted, or a creation that failed
    // part-way); refuse it rather than let callers dereference null.
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

    @Transactional
    public Iterable<User> getAllUsers() {
        return userRepository.findByMunicipalityId(getUser().getMunicipality().getId());
    }

    @Transactional
    public Municipality getMunicipality() {
        return getUser().getMunicipality();
    }

    public User create(UserContract userContract) {
            // Resolve the role before creating in Auth0: creation is irreversible, so a role
            // failure afterwards would leave an orphaned account.
            String roleName = roleNameFor(userContract);
            String roleId = auth0Service.getRoleIdByName(roleName);

            ResponseEntity<Object> response;
            try {
                response = auth0Service.createUser(userContract);
            } catch (HttpClientErrorException.Conflict alreadyExists) {
                return adoptExistingAccount(userContract, roleName, roleId, alreadyExists);
            }
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AuthorizationServiceException("Unable to create user");
            }
            LinkedHashMap<String, Object> authRes = (LinkedHashMap) response.getBody();
            User user = new User();
            user.setUserName((String) authRes.get("user_id"));
            user.setEmail((String) authRes.get("email"));
            user.setName((String) authRes.get("name"));
            user.setRole(roleName);
            user.setAdmin(ADMIN_USER_ROLE.equals(roleName));
            user.setMunicipality(municipalityService.getMunicipality(userContract.getMunicipalityId()));
            User savedUser = assignRolesAndSaveUser(roleName, roleId, user);
            sendPasswordSetupEmail(savedUser);
            return savedUser;
    }

    // A 409 means either a live user owns the address (refuse), or an account exists that we
    // have no active record of — an orphan from a failed creation, or a deleted user (adopt).
    // Leftover Auth0 roles are stripped first so the account cannot keep old privileges.
    private User adoptExistingAccount(UserContract userContract, String roleName, String roleId,
                                      HttpClientErrorException.Conflict conflict) {
        Map<String, Object> existingAccount = auth0Service.findUserByEmail(userContract.getEmail());
        if (existingAccount == null) {
            throw conflict;
        }
        String auth0UserId = (String) existingAccount.get("user_id");
        User liveUser = userRepository.findByUserName(auth0UserId);
        if (liveUser != null) {
            throw conflict;
        }

        logger.info("Auth0 already holds {} with no active record here — adopting it rather than "
                + "refusing the address permanently.", userContract.getEmail());

        User user = new User();
        user.setUserName(auth0UserId);
        user.setEmail(userContract.getEmail());
        user.setName(userContract.getName());
        user.setRole(roleName);
        user.setAdmin(ADMIN_USER_ROLE.equals(roleName));
        user.setMunicipality(municipalityService.getMunicipality(userContract.getMunicipalityId()));

        List<String> leftoverRoles = auth0Service.roleIdsOf(user);
        if (!leftoverRoles.isEmpty()) {
            auth0Service.removeRole(user, leftoverRoles);
        }
        User savedUser = assignRolesAndSaveUser(roleName, roleId, user);
        sendPasswordSetupEmail(savedUser);
        return savedUser;
    }

    // Auth0 creates the account with a random password nobody sees; the change-password mail
    // is the invitation. A send failure is logged, not thrown: the account already exists.
    private void sendPasswordSetupEmail(User user) {
        try {
            auth0Service.sendChangePasswordEmail(user);
        } catch (Exception e) {
            logger.warn("User {} was created but the password-setup email could not be sent. "
                    + "They cannot sign in until it is re-sent or they use 'Forgot password'.",
                    user.getEmail(), e);
        }
    }

    // An explicit role wins over the isAdmin flag; with no role given, derive it from the flag.
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
            // The cached id may be stale (role recreated in Auth0); drop it so the next attempt re-resolves.
            auth0Service.forgetRole(roleName);
            throw new AuthorizationServiceException("Unable to assign roles to user");
        }
        return save(user);
    }

    public User sendChangePasswordEmail(User user) {
        auth0Service.sendChangePasswordEmail(user);
        return user;
    }

    // Keep the existing role unless the request names one or changes the admin flag: the
    // update screens send only the flag, and deriving from it would silently alter Read-only users.
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

    // Not @Transactional: the Auth0 call cannot be rolled back, so a transaction gains nothing.
    public User update(Long userId, UserContract userContract) {
        User user = getUser(userId);
        String currentRole = user.getRole();
        String newRole = roleNameForUpdate(userContract, user);

        if (!newRole.equals(currentRole)) {
            applyRoleChange(user, currentRole, newRole);
            user.setRole(newRole);
            user.setAdmin(ADMIN_USER_ROLE.equals(newRole));
        }
        user.setName(userContract.getName());
        return save(user);
    }

    // Change Auth0 first so a remote failure aborts the update. Remove the old role before
    // adding the new one: failing with no role is recoverable, failing with both is not.
    private void applyRoleChange(User user, String currentRole, String newRole) {
        if (existsOnlyLocally(user)) {
            logger.info("{} exists only in this database, so there is no Auth0 role to change.",
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

    // Seeded local-dev users ("local|...") have no Auth0 identity, so there is nothing remote to sync.
    private boolean existsOnlyLocally(User user) {
        String userName = user.getUserName();
        return userName == null || userName.startsWith("local|");
    }


    // Revoke in Auth0 before voiding the row. Not @Transactional, for the same reason as update.
    public User delete(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        revokeRemoteAccess(user);
        userRepository.delete(user);
        return user;
    }

    // Strip every Auth0 role so any token issued carries no permissions. Auth0's "block" would
    // be tidier but is a PATCH, which the HTTP client cannot send (see RestTemplateHttpMethodsTest).
    private void revokeRemoteAccess(User user) {
        if (existsOnlyLocally(user)) {
            logger.info("{} exists only in this database, so there is no Auth0 access to revoke.",
                    user.getEmail());
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
