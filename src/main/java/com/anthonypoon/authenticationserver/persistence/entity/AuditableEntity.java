package com.anthonypoon.authenticationserver.persistence.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@SuperBuilder
public abstract class AuditableEntity {
    private ZonedDateTime createdAt;
    private ZonedDateTime modifiedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now();
        this.modifiedAt = ZonedDateTime.now();
    }

    @PreUpdate
    public void PreUpdate() {
        this.modifiedAt = ZonedDateTime.now();
    }
}
