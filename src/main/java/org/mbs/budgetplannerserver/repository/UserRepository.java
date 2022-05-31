package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    public User findByUserName(String userName);
}
