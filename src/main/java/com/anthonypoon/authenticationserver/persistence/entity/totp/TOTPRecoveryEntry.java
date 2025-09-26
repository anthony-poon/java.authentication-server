package com.anthonypoon.authenticationserver.persistence.entity.totp;

import com.anthonypoon.authenticationserver.persistence.entity.AuditableEntity;
import com.anthonypoon.authenticationserver.persistence.entity.user.ApplicationUserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "totp_recovery")
public class TOTPRecoveryEntry extends AuditableEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TOTP_RECOVERY_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TOTP_RECOVERY_ID")
    private Long id;

    @Column(nullable = false)
    private String value;

    @ManyToOne(optional = false)
    private ApplicationUserEntity user;

    private Instant consumedAt;
}
