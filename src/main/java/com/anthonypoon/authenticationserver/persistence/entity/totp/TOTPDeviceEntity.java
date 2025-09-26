package com.anthonypoon.authenticationserver.persistence.entity.totp;

import com.anthonypoon.authenticationserver.persistence.entity.AuditableEntity;
import com.anthonypoon.authenticationserver.persistence.entity.user.ApplicationUserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name = "totp_device")
public class TOTPDeviceEntity extends AuditableEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TOTP_DEVICE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TOTP_DEVICE_ID")
    private Long id;

    private String deviceName;

    private String secret;

    @Column(nullable = false)
    private boolean isValidated;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ApplicationUserEntity user;

    private Instant lastUsedAt;
}
