package com.anthonypoon.authenticationserver.service.auth;

import com.anthonypoon.authenticationserver.persistence.entity.ApplicationUser;
import com.anthonypoon.authenticationserver.persistence.entity.UserProfile;
import com.anthonypoon.authenticationserver.persistence.repository.ApplicationUserRepository;
import com.anthonypoon.authenticationserver.service.auth.data.UserRegistrationData;
import com.anthonypoon.authenticationserver.service.auth.data.UserUpdateData;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleDuplicatedException;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrincipleNotFoundException;
import com.anthonypoon.authenticationserver.service.auth.exception.UserPrinciplePasswordException;
import com.anthonypoon.authenticationserver.service.auth.policy.PasswordPolicy;
import com.anthonypoon.authenticationserver.service.auth.principle.UserPrinciple;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserPrincipleService {
    private final ApplicationUserRepository users;
    private final PasswordEncoder encoder;
    private final PasswordPolicy policy;

    public UserPrincipleService(ApplicationUserRepository users, PasswordEncoder encoder, PasswordPolicy policy) {
        this.users = users;
        this.encoder = encoder;
        this.policy = policy;
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
        var users = new ArrayList<ApplicationUser>();
        for (UserRegistrationData datum : data) {
            // TODO: Audit log
            this.policy.validate(datum.getPassword());
            var user = ApplicationUser.builder()
                    .username(datum.getUsername())
                    .password(encoder.encode(datum.getPassword()))
                    .identifier(UUID.randomUUID().toString())
                    .email(datum.getEmail())
                    .isEnabled(true)
                    .isValidated(datum.isValidated())
                    .roles(datum.getRoles())
                    .build();
            var profile = UserProfile.builder()
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
                .collect(Collectors.toMap(ApplicationUser::getId, Function.identity()));
        for (UserUpdateData datum : data) {
            var user = currents.get(datum.getUserId());
            if (user == null) {
                throw new UserPrincipleNotFoundException("Unable to update principle by id.");
            }
            user.setEnabled(datum.isEnabled());
            user.setValidated(datum.isValidated());
            user.setRoles(datum.getRoles());
            if (StringUtils.isNotEmpty(datum.getPassword())) {
                // TODO: Audit log
                user.setPassword(encoder.encode(datum.getPassword()));
            }
        }
        this.users.saveAll(currents.values());
        return currents.values().stream().map(UserPrinciple::getInstance).collect(Collectors.toList());
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

    private Optional<UserPrinciple> build(ApplicationUser user) {
        if (user == null) {
            return Optional.empty();
        }
        return Optional.of(UserPrinciple.getInstance(user));
    }

}
