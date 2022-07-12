package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Translation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationRepository extends CrudRepository<Translation, Long> {
}


