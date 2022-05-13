package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Municipality;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipalityRepository extends CrudRepository<Municipality, Long> {

}
