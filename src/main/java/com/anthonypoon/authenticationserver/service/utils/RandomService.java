package com.anthonypoon.authenticationserver.service.utils;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.stereotype.Service;

@Service
public class RandomService {
    private final static RandomStringGenerator generator = new RandomStringGenerator.Builder()
            .withinRange('0', 'z')
            .filteredBy(Character::isLetterOrDigit)
            .get();

    public String alphanumeric(int length) {
        return generator.generate(length);
    }
}
