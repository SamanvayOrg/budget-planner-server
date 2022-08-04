package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.AuthRole;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.AuthRoleRepository;
import org.mbs.budgetplannerserver.repository.UserRepository;
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
    public static final String REGULAR_USER_ROLE = "RegularUser";
    public static final String ADMIN_USER_ROLE = "Admin";

    private final MunicipalityService municipalityService;
    private final UserRepository userRepository;
    private final Auth0Service auth0Service;
    private final AuthRoleRepository authRoleRepository;

    @Autowired
    public UserService(MunicipalityService municipalityService, UserRepository userRepository, Auth0Service auth0Service, AuthRoleRepository authRoleRepository) {
        this.municipalityService = municipalityService;
        this.userRepository = userRepository;
        this.auth0Service = auth0Service;
        this.authRoleRepository = authRoleRepository;
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
    public Iterable<User> getAllUsers() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByMunicipalityId(userRepository.findByUserName(userName).getMunicipality().getId());
    }

    @Transactional
    public Municipality getMunicipality() {
        return getUser().getMunicipality();
    }

    @Transactional
    public User create(UserContract userContract) {
        ResponseEntity<Object> response = auth0Service.createUser(userContract);
        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new AuthorizationServiceException("Unable to create user");
        }
        LinkedHashMap<String, Object> authRes = (LinkedHashMap)response.getBody();
        User user = new User();
        user.setUserName((String) authRes.get("user_id"));
        user.setEmail((String) authRes.get("email"));
        user.setName((String) authRes.get("name"));
        user.setAdmin(userContract.getAdmin());
        user.setMunicipality(municipalityService.getMunicipality(userContract.getMunicipalityId()));
        return assignRolesAndSaveUser(userContract, user);
    }

    private User assignRolesAndSaveUser(UserContract userContract, User user) {
        Optional<AuthRole> authRole = authRoleRepository.findByRoleName(userContract.getAdmin() ?
                ADMIN_USER_ROLE : REGULAR_USER_ROLE);
        ResponseEntity<String> response = auth0Service.assignRole(user,
                Arrays.asList(authRole.orElseThrow(EntityNotFoundException::new).getRoleId()));
        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new AuthorizationServiceException("Unable to assign roles to user");
        }
        return save(user);
    }

    @Transactional
    public User update(Long userId, UserContract userContract) {
        User user = getUser(userId);
        user.setName(userContract.getName());
        user.setAdmin(userContract.getAdmin());
        return save(user);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
