package com.anthonypoon.authenticationserver.persistence.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UserProfileEntity {
    @Id
    @SequenceGenerator(name = "SEQ_USER_PROFILE_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_USER_PROFILE_ID")
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private ApplicationUserEntity user;

    private String displayName;
}
