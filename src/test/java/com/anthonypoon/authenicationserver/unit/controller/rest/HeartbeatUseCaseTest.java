package com.anthonypoon.authenicationserver.unit.controller.rest;


import com.anthonypoon.authenicationserver.mock.DateTimeMock;
import com.anthonypoon.authenticationserver.controller.rest.index.usecase.HeartbeatUseCase;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeartbeatUseCaseTest {
    @Test
    public void testCanReturnHeartbeat() {
        var expectation = ZonedDateTime.now();
        var dt = DateTimeMock.getMocked(expectation);
        var service = new HeartbeatUseCase("some-application", dt);
        var response = service.getHeartbeat();
        assertTrue(expectation.isEqual(response.getTimestamp()));
        assertEquals("some-application", response.getApplication());
    }


}
