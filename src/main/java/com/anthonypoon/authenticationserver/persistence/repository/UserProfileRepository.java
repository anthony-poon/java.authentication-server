package com.anthonypoon.authenticationserver.persistence.repository;

import com.anthonypoon.authenticationserver.persistence.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long id);
}
