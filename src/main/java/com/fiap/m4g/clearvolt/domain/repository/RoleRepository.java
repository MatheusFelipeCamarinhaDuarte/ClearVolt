package com.fiap.m4g.clearvolt.domain.repository;

import com.fiap.m4g.clearvolt.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
