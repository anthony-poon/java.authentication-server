package com.anthonypoon.authenticationserver.persistence.repository.token;

import com.anthonypoon.authenticationserver.persistence.entity.token.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    @Query("SELECT t " +
            "FROM RefreshTokenEntity t " +
            "WHERE t.expireAt < :now " +
            "AND t.consumedAt IS NULL " +
            "AND t.tokenValue = :tokenValue ")
    Optional<RefreshTokenEntity> findByTokenValue(String tokenValue, Instant now);
}
