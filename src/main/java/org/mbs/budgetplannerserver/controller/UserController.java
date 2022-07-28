package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/api/users", method = GET)
    @ResponseBody
    public Iterable<User> getAllUsers() {
      return  userService.getAllUsers();
    }
}
