package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.mapper.Auth0UserResponse;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    public static final List<String> REGULAR_USER_ROLES = Arrays.asList("read", "write");
    public static final List<String> ADMIN_USER_ROLES = Arrays.asList("read", "write", "admin");
    private final MunicipalityService municipalityService;
    private final UserRepository userRepository;

    private final Auth0Service auth0Service;

    @Autowired
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
        ResponseEntity<Auth0UserResponse> response = auth0Service.createUser(userContract);
        if(!response.getStatusCode().is2xxSuccessful()) {
            throw new AuthorizationServiceException("Unable to create user");
        }
        Auth0UserResponse authRes = response.getBody();
        User user = new User();
        user.setUserName(authRes.getUserId());
        user.setEmail(authRes.getEmail());
        user.setName(authRes.getName());
        user.setAdmin(userContract.getAdmin());
        user.setMunicipality(municipalityService.getMunicipality(userContract.getMunicipalityId()));
        return assignRolesAndSaveUser(userContract, response, user);
    }

    private User assignRolesAndSaveUser(UserContract userContract, ResponseEntity<Auth0UserResponse> response, User user) {
        auth0Service.assignRole(user, userContract.getAdmin() ? ADMIN_USER_ROLES : REGULAR_USER_ROLES);
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
