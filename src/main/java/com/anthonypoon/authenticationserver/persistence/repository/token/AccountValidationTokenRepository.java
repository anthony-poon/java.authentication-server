package com.anthonypoon.authenticationserver.persistence.repository.token;

import com.anthonypoon.authenticationserver.persistence.entity.token.AccountValidationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountValidationTokenRepository extends JpaRepository<AccountValidationTokenEntity, Long> {
    @Query("SELECT t FROM AccountValidationTokenEntity t " +
            "WHERE t.consumedAt IS NULL " +
            "AND t.identifier = :identifier")
    List<AccountValidationTokenEntity> findAllByIdentifier(String identifier);

    @Query("SELECT t FROM AccountValidationTokenEntity t " +
            "WHERE t.consumedAt IS NULL " +
            "AND t.tokenValue = :tokenValue")
    Optional<AccountValidationTokenEntity> findByTokenValue(String tokenValue);
}
