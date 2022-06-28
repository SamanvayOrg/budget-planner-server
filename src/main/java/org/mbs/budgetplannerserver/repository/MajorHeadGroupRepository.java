package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.code.MajorHeadGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorHeadGroupRepository extends CrudRepository<MajorHeadGroup, Long> {
}
