package com.anthonypoon.authenticationserver.persistence.entity.user;

import com.anthonypoon.authenticationserver.domains.role.UserRole;
import com.anthonypoon.authenticationserver.persistence.entity.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Table(name = "application_user")
public class ApplicationUserEntity extends AuditableEntity {
    @Id
    @SequenceGenerator(name = "SEQ_APPLICATION_USER_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_APPLICATION_USER_ID")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(unique = true)
    private String email;

    private String password;

    private boolean isEnabled;

    private boolean isValidated;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "APPLICATION_USER_ROLES", joinColumns = @JoinColumn(name = "application_user_id"))
    @Column(name = "application_user_role")
    private Set<UserRole> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfileEntity profile;
}
