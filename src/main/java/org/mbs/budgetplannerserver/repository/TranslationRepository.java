package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Translation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TranslationRepository extends CrudRepository<Translation, Long> {
    Iterable<Translation> findByStateId(Long stateId);
}


