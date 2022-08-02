package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.repository.CityClassRepository;
import org.mbs.budgetplannerserver.repository.MunicipalityRepository;
import org.mbs.budgetplannerserver.repository.StateRepository;
import org.mbs.budgetplannerserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
public class UserService {
    private final MunicipalityService municipalityService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(MunicipalityService municipalityService, UserRepository userRepository) {
        this.municipalityService = municipalityService;
        this.userRepository = userRepository;
    }

    @Transactional
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
    public User create(UserContract userContract) {
        User user = new User();
        user.setUserName(userContract.getUserName());
        user.setName(userContract.getName());
        user.setAdmin(userContract.getAdmin());
        user.setMunicipality(municipalityService.getMunicipality(userContract.getMunicipalityId()));
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
