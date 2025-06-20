package com.anthonypoon.authenticationserver.persistence.repository.user;

import com.anthonypoon.authenticationserver.persistence.entity.user.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {
    Optional<UserProfileEntity> findByUserId(Long id);
}
