package com.anthonypoon.authenticationserver.persistence.repository.token;

import com.anthonypoon.authenticationserver.persistence.entity.token.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    @Query("SELECT t " +
            "FROM RefreshTokenEntity t " +
            "WHERE t.expireAt < :now " +
            "AND t.consumedAt IS NULL " +
            "AND t.tokenValue = :tokenValue ")
    Optional<RefreshTokenEntity> findByTokenValue(String tokenValue, Instant now);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshTokenEntity t SET t.consumedAt = :now WHERE t.identifier = :identifier AND t.consumedAt IS NULL")
    void invalidate(String identifier, Instant now);
}
