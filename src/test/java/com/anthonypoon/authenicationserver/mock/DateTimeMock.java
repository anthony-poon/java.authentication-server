package com.anthonypoon.authenicationserver.mock;

import com.anthonypoon.authenticationserver.service.utils.DateTimeService;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DateTimeMock {
    public static DateTimeService getMocked(ZonedDateTime now) {
        var dt = mock(DateTimeService.class);
        when(dt.now()).thenReturn(now);
        return dt;
    }
}
