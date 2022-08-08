package org.mbs.budgetplannerserver.service;

import org.mbs.budgetplannerserver.domain.State;
import org.mbs.budgetplannerserver.repository.StateRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class StateService {

    private final StateRepository stateRepository;

    public StateService(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public State getState(String name) {
        return stateRepository.findByName(name).orElseThrow(NOT_FOUND());
    }
    private Supplier<ResponseStatusException> NOT_FOUND() {
        return () -> new ResponseStatusException(NOT_FOUND, "entity not found");
    }
}
