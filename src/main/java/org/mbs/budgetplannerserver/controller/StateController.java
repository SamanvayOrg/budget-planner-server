package org.mbs.budgetplannerserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mbs.budgetplannerserver.contract.StateContract;
import org.mbs.budgetplannerserver.mapper.StateContractMapper;
import org.mbs.budgetplannerserver.service.StateService;
import org.mbs.budgetplannerserver.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class StateController {
    private final UserService userService;

    public StateController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/api/state", method = GET)
    @ResponseBody
    public StateContract getState() throws JsonProcessingException {
       return new StateContractMapper().fromState(userService.getUser().getState());
    }
}
