package com.anthonypoon.authenticationserver.persistence.repository.user;

import com.anthonypoon.authenticationserver.persistence.entity.user.ApplicationUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUserEntity, Long> {
    Optional<ApplicationUserEntity> findByIdentifier(String hash);
    Optional<ApplicationUserEntity> findByUsername(String username);
    List<ApplicationUserEntity> findAllByUsernameIn(List<String> username);
    Optional<ApplicationUserEntity> findByEmail(String email);
    List<ApplicationUserEntity> findAllByEmailIn(List<String> emails);
    List<ApplicationUserEntity> findAllByIdIn(List<Long> id);
}
