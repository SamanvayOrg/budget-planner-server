package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.Municipality;
import org.mbs.budgetplannerserver.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUserName(String userName);
    Iterable<User> findByMunicipalityId(Long municipalityId);
    Iterable<User> findByIsAdmin(Boolean isAdmin);
}
