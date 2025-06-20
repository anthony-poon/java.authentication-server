package com.anthonypoon.authenticationserver.persistence.entity.token;

import com.anthonypoon.authenticationserver.persistence.entity.user.ApplicationUserEntity;
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
public class RefreshTokenEntity extends TokenEntity {
    @Id
    @SequenceGenerator(name = "SEQ_REFRESH_TOKEN_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_REFRESH_TOKEN_ID")
    private Long id;
}
