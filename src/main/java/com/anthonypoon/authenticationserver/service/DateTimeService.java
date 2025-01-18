package com.anthonypoon.authenticationserver.service;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class DateTimeService {
    public ZonedDateTime now() {
        return ZonedDateTime.now();
    }
}
