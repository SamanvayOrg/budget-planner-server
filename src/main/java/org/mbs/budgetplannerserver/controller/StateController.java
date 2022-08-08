package org.mbs.budgetplannerserver.controller;

import org.mbs.budgetplannerserver.contract.StateContract;
import org.mbs.budgetplannerserver.mapper.StateContractMapper;
import org.mbs.budgetplannerserver.service.StateService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class StateController {
    private final StateService stateService;

    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @RequestMapping(value = "/api/state", method = GET)
    @ResponseBody
    public StateContract getState(@RequestParam("name") String name) {
       return new StateContractMapper().fromState(stateService.getState(name));
    }
}
