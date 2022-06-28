package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.code.FunctionGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionGroupRepository extends CrudRepository<FunctionGroup, Long> {
}
