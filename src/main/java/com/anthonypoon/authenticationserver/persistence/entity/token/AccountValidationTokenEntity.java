package com.anthonypoon.authenticationserver.persistence.entity.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "token_account_validation",
        uniqueConstraints = { @UniqueConstraint(columnNames = { "identifier", "token_value" }) }
)
public class AccountValidationTokenEntity extends TokenEntity {
    @Id
    @SequenceGenerator(name = "SEQ_TOKEN_ACCOUNT_VALIDATION_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_TOKEN_ACCOUNT_VALIDATION_ID")
    private Long id;

    // Store the user identifier directly instead of a @ManyToOne mapping.
    // This avoids introducing a foreign key constraint, which can complicate deletion
    // of users or force unwanted cascading behavior. It also decouples token persistence
    // from the user entity lifecycle, which is desirable since tokens are temporary and
    // managed separately from core user data.
    @Column(nullable = false)
    private String identifier;
}
