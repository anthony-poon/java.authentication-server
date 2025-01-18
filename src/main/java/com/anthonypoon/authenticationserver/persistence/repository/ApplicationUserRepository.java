package com.anthonypoon.authenticationserver.persistence.repository;

import com.anthonypoon.authenticationserver.persistence.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByIdentifier(String hash);
    Optional<ApplicationUser> findByUsername(String username);
    List<ApplicationUser> findAllByUsernameIn(List<String> username);
    Optional<ApplicationUser> findByEmail(String email);

    List<ApplicationUser> findAllByEmailIn(List<String> emails);
    List<ApplicationUser> findAllByIdIn(List<Long> id);
}
