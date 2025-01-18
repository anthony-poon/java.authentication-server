package com.anthonypoon.authenticationserver.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitializationEntry extends AuditableEntity {
    @Id
    @SequenceGenerator(name = "SEQ_INITIALIZATION_ENTRY_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_INITIALIZATION_ENTRY_ID")
    private Long id;
}
