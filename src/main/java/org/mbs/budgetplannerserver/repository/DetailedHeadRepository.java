package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.code.DetailedHead;
import org.springframework.data.repository.CrudRepository;

public interface DetailedHeadRepository extends CrudRepository<DetailedHead, Long> {
    DetailedHead findByFullCode(String fullCode);
}
