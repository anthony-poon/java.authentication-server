package com.anthonypoon.authenticationserver.service.auth;

import com.anthonypoon.authenticationserver.persistence.entity.user.ApplicationUserEntity;
import com.anthonypoon.authenticationserver.persistence.entity.user.UserProfileEntity;
import com.anthonypoon.authenticationserver.persistence.repository.user.ApplicationUserRepository;
import com.anthonypoon.authenticationserver.service.auth.data.UserRegistrationData;
import com.anthonypoon.authenticationserver.service.auth.data.UserUpdateData;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleDuplicatedException;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleNotFoundException;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrinciplePasswordException;
import com.anthonypoon.authenticationserver.service.auth.policy.PasswordPolicy;
import com.anthonypoon.authenticationserver.domains.auth.UserPrinciple;
import com.anthonypoon.authenticationserver.service.encryption.EncryptionService;
import com.google.common.io.BaseEncoding;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserPrincipleService {
    private final ApplicationUserRepository users;
    private final PasswordEncoder encoder;
    private final PasswordPolicy policy;
    private final EncryptionService encryption;

    public UserPrincipleService(ApplicationUserRepository users, PasswordEncoder encoder, PasswordPolicy policy, EncryptionService encryption) {
        this.users = users;
        this.encoder = encoder;
        this.policy = policy;
        this.encryption = encryption;
    }

    @Transactional
    public List<UserPrinciple> register(List<UserRegistrationData> data) throws UserPrincipleDuplicatedException, UserPrinciplePasswordException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("User registration data must not be null");
        }
        var usernames = data.stream().map(UserRegistrationData::getUsername).toList();
        var existing = this.users.findAllByUsernameIn(usernames);
        if (!existing.isEmpty()) {
            throw new UserPrincipleDuplicatedException("Registration data contains duplicated username", UserPrincipleDuplicatedException.Type.USERNAME);
        }
        var emails  = data.stream().map(UserRegistrationData::getEmail).toList();
        existing = this.users.findAllByEmailIn(emails);
        if (!existing.isEmpty()) {
            throw new UserPrincipleDuplicatedException("Registration data contains duplicated email", UserPrincipleDuplicatedException.Type.EMAIL);
        }
        var users = new ArrayList<ApplicationUserEntity>();
        for (UserRegistrationData datum : data) {
            // TODO: Audit log
            this.policy.validate(datum.getPassword());
            var user = ApplicationUserEntity.builder()
                    .username(datum.getUsername())
                    .password(encoder.encode(datum.getPassword()))
                    .identifier(deriveIdentifier(datum))
                    .email(datum.getEmail())
                    .isEnabled(true)
                    .isValidated(datum.isValidated())
                    .roles(datum.getRoles())
                    .build();
            var profile = UserProfileEntity.builder()
                    .user(user)
                    .displayName(datum.getDisplayName())
                    .build();
            user.setProfile(profile);
            users.add(user);
        }
        this.users.saveAll(users);
        return users.stream().map(UserPrinciple::getInstance).collect(Collectors.toList());
    }

    @Transactional
    public UserPrinciple register(UserRegistrationData data) throws UserPrincipleDuplicatedException, UserPrinciplePasswordException {
        if (data == null) {
            throw new IllegalArgumentException("User registration data must not be null");
        }
        var users = this.register(List.of(data));
        return users.get(0);
    }

    @Transactional
    public List<UserPrinciple> update(List<UserUpdateData> data) throws UserPrincipleNotFoundException {
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("User update data must not be null or empty");
        }
        var ids = data.stream().map(UserUpdateData::getUserId).toList();
        var currents = this.users.findAllByIdIn(ids).stream()
                .collect(Collectors.toMap(ApplicationUserEntity::getId, Function.identity()));
        for (UserUpdateData datum : data) {
            var user = currents.get(datum.getUserId());
            if (user == null) {
                throw new UserPrincipleNotFoundException("Unable to update principle by id.");
            }
            user.setEnabled(datum.isEnabled());
            user.setValidated(datum.isValidated());
            user.setRoles(datum.getRoles());
        }
        this.users.saveAll(currents.values());
        return currents.values().stream().map(UserPrinciple::getInstance).collect(Collectors.toList());
    }

    @Transactional
    public void changePassword(UserPrinciple user, String password) throws UserPrincipleNotFoundException {
        var entity = this.users.findById(user.getId())
                .orElseThrow(() -> new UserPrincipleNotFoundException("Unable to update password by id."));
        var hash = this.encoder.encode(password);
        // TODO: Audit
        entity.setPassword(hash);
    }

    @Transactional
    public UserPrinciple update(UserUpdateData data) throws UserPrincipleNotFoundException {
        if (data == null) {
            throw new IllegalArgumentException("User update data must not be null");
        }
        var users = this.update(List.of(data));
        return users.get(0);
    }

    public Optional<UserPrinciple> getByUsername(String username) {
        var user = this.users.findByUsername(username)
                .orElse(null);
        return this.build(user);
    }

    public Optional<UserPrinciple> getByIdentifier(String identifier) {
        var user = this.users.findByIdentifier(identifier)
                .orElse(null);
        return this.build(user);
    }

    private Optional<UserPrinciple> build(ApplicationUserEntity user) {
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(UserPrinciple.getInstance(user));
    }

    private String deriveIdentifier(UserRegistrationData datum) {
        return BaseEncoding.base32().encode(this.encryption.hash(datum.getUsername()));
    }
}
