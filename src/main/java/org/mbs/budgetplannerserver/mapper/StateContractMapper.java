package org.mbs.budgetplannerserver.mapper;

import org.mbs.budgetplannerserver.contract.StateContract;
import org.mbs.budgetplannerserver.domain.State;

import java.util.ArrayList;
import java.util.List;

public class StateContractMapper {
    public Iterable<StateContract> map(Iterable<State> states) {
        List<StateContract> stateContract = new ArrayList<>();
        states.forEach(e -> {
            stateContract.add(fromState(e));
        });
        return stateContract;
    }

    public StateContract fromState(State state) {
        StateContract stateContract = new StateContract();
        stateContract.setName(state.getName());
        stateContract.setLanguages(state.getLanguages());
        return stateContract;
    }
}
