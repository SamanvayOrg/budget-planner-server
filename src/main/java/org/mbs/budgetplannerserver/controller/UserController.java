package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.UserContract;
import org.mbs.budgetplannerserver.domain.User;
import org.mbs.budgetplannerserver.mapper.UserContractMapper;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/api/users", method = GET)
    @ResponseBody
    public Iterable<User> getAllUsers() {
        return userService.getAllUsers();
//        return new UserContractMapper().map( userService.getAllUsers());
    }

    @RequestMapping(value = "/api/user", method = POST)
    public void updateUser(@RequestBody User user) {
        userService.save(user);
    }

    @RequestMapping(value = "api/user",method = GET)
    public UserContract getUser(){
        return new UserContractMapper().getUser(userService.getUser());
    }
}
