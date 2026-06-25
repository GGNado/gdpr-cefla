package com.cefla.iot.gdpr.repository.auth;

import com.cefla.iot.gdpr.entity.auth.Role;
import com.cefla.iot.gdpr.entity.auth.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
