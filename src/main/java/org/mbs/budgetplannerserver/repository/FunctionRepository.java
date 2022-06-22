package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.code.Function;
import org.springframework.data.repository.CrudRepository;

public interface FunctionRepository  extends CrudRepository<Function, Long> {
    Function findByFullCode(String fullCode);
}
