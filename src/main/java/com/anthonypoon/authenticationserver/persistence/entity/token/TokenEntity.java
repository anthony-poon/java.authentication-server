package com.anthonypoon.authenticationserver.persistence.entity.token;

import com.anthonypoon.authenticationserver.persistence.entity.AuditableEntity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@MappedSuperclass
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public abstract class TokenEntity extends AuditableEntity {
    private String tokenValue;
    private Instant expireAt;
    private Instant consumedAt;
}
