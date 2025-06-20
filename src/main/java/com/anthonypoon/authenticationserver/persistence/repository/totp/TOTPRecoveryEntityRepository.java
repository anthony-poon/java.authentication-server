package com.anthonypoon.authenticationserver.persistence.repository.totp;

import com.anthonypoon.authenticationserver.persistence.entity.totp.TOTPRecoveryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TOTPRecoveryEntityRepository extends JpaRepository<TOTPRecoveryEntry, Long> {
    @Query("SELECT r FROm TOTPRecoveryEntry r " +
            "WHERE r.user.id = ?1 " +
            "AND r.user.isValidated = true " +
            "AND r.user.isEnabled = true " +
            "AND r.consumedAt IS NULL")
    List<TOTPRecoveryEntry> findAllValidByUser(Long id);
}
