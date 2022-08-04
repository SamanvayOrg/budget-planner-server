package org.mbs.budgetplannerserver.repository;

import org.mbs.budgetplannerserver.domain.AuthRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRoleRepository extends CrudRepository<AuthRole, Long> {
    Optional<AuthRole> findByRoleName(String roleName);
}
