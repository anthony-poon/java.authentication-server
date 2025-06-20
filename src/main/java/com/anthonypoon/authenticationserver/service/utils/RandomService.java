package com.anthonypoon.authenticationserver.service.utils;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RandomService {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final static RandomStringGenerator generator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(Character::isLetterOrDigit)
            .get();

    public String alphanumeric(int length) {
        return generator.generate(length);
    }

    public byte[] secure(int length) {
        var arr = new byte[length];
        SECURE_RANDOM.nextBytes(arr);
        return arr;
    }

    public int nextInt(int bound) {
        return SECURE_RANDOM.nextInt(bound);
    }
}
