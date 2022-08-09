package org.mbs.budgetplannerserver.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.mbs.budgetplannerserver.contract.StateContract;
import org.mbs.budgetplannerserver.domain.State;

import java.util.ArrayList;
import java.util.List;

public class StateContractMapper {
    public Iterable<StateContract> map(Iterable<State> states) {
        List<StateContract> stateContract = new ArrayList<>();
        states.forEach(e -> {
            try {
                stateContract.add(fromState(e));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
        return stateContract;
    }

    public StateContract fromState(State state) throws JsonProcessingException {
        StateContract stateContract = new StateContract();
        stateContract.setId(state.getId());
        stateContract.setName(state.getName());
        stateContract.setLanguages(state.getLanguages());
        return stateContract;
    }
}
