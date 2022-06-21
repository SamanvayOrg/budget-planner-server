package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MunicipalityService {
    private final UserService userService;

    @Autowired
    public MunicipalityService(UserService userService) {
        this.userService = userService;
    }

    public Municipality getMunicipality() {
        User user = userService.getUser();
        return user.getMunicipality();
    }
}
