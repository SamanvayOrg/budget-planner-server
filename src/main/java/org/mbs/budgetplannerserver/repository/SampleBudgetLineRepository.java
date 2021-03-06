package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.SampleBudgetLine;
import org.mbs.budgetplannerserver.domain.State;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SampleBudgetLineRepository  extends CrudRepository<SampleBudgetLine, Long> {
    List<SampleBudgetLine> findAllByState(State state);
}
