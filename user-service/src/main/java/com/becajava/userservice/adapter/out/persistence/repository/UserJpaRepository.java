package com.becajava.userservice.adapter.out.persistence.repository;

import com.becajava.userservice.adapter.out.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    boolean existsByEmail(String email);
    Optional<UserJpaEntity> findByEmail(String email);
}
