package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static final String REGULAR_USER_ROLE = "RegularUser";
    public static final String ADMIN_USER_ROLE = "Admin";

    private final MunicipalityService municipalityService;
    private final UserRepository userRepository;
    private final Auth0Service auth0Service;

    @Autowired
    // AuthRoleRepository is deliberately no longer a dependency: role ids now come from
    // Auth0 by name (Auth0Service#getRoleIdByName) rather than from the auth_role table,
    // whose seeded values are tenant-specific and silently wrong on any other tenant.
    public UserService(MunicipalityService municipalityService, UserRepository userRepository, Auth0Service auth0Service) {
        this.municipalityService = municipalityService;
        this.userRepository = userRepository;
        this.auth0Service = auth0Service;
    }


    public User getUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserName(userName);
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
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByMunicipalityId(userRepository.findByUserName(userName).getMunicipality().getId());
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
            String roleId = auth0Service.getRoleIdByName(roleNameFor(userContract));

            ResponseEntity<Object> response = auth0Service.createUser(userContract);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AuthorizationServiceException("Unable to create user");
            }
            LinkedHashMap<String, Object> authRes = (LinkedHashMap) response.getBody();
            User user = new User();
            user.setUserName((String) authRes.get("user_id"));
            user.setEmail((String) authRes.get("email"));
            user.setName((String) authRes.get("name"));
            user.setAdmin(userContract.getAdmin());
            user.setMunicipality(municipalityService.getMunicipality(userContract.getMunicipalityId()));
            User savedUser = assignRolesAndSaveUser(roleId, user);
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

    private String roleNameFor(UserContract userContract) {
        return Boolean.TRUE.equals(userContract.getAdmin()) ? ADMIN_USER_ROLE : REGULAR_USER_ROLE;
    }

    private User assignRolesAndSaveUser(String roleId, User user) {
        ResponseEntity<String> response = auth0Service.assignRole(user, Arrays.asList(roleId));
        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new AuthorizationServiceException("Unable to assign roles to user");
        }
        return save(user);
    }

    public User sendChangePasswordEmail(User user) {
        auth0Service.sendChangePasswordEmail(user);
        return user;
    }

    @Transactional
    public User update(Long userId, UserContract userContract) {
        User user = getUser(userId);
        user.setName(userContract.getName());
        user.setAdmin(userContract.getAdmin());
        return save(user);
    }


    @Transactional
    public User delete(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        userRepository.delete(user);
        return user;
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
