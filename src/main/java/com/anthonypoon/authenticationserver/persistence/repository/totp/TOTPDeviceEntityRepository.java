package com.anthonypoon.authenticationserver.persistence.repository.totp;

import com.anthonypoon.authenticationserver.persistence.entity.totp.TOTPDeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TOTPDeviceEntityRepository extends JpaRepository<TOTPDeviceEntity, Long> {
    @Query("SELECT COUNT(d) > 0 " +
            "FROM TOTPDeviceEntity d " +
            "WHERE d.user.id = ?1 " +
            "AND d.isValidated = true ")
    boolean existsByUserId(Long userId);

    @Query("SELECT d FROM TOTPDeviceEntity d " +
            "WHERE d.id = ?1 " +
            "AND d.user.id = ?2 ")
    Optional<TOTPDeviceEntity> findByIdAndUser(Long id, Long userId);

    @Query("SELECT d from TOTPDeviceEntity d " +
            "WHERE d.user.id = ?1 " +
            "AND d.isValidated = true")
    List<TOTPDeviceEntity> findAllValidByUser(Long userId);
}
