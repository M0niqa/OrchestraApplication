package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.Role;
import com.monika.worek.orchestra.model.UserRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRoleRepository extends CrudRepository<UserRole, Long> {
    Optional<UserRole> findByName(Role roleName);
}
