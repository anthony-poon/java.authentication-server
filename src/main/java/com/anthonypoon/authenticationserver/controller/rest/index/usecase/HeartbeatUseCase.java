package com.anthonypoon.authenticationserver.controller.rest.index.usecase;

import com.anthonypoon.authenticationserver.controller.rest.index.response.HeartbeatResponse;
import com.anthonypoon.authenticationserver.service.utils.DateTimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HeartbeatUseCase {
    private final String name;
    private final DateTimeService dateTime;
    public HeartbeatUseCase(
            @Value("${spring.application.name}") String name,
            DateTimeService dateTime
    ) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public HeartbeatResponse getHeartbeat() {
        return HeartbeatResponse.builder()
                .application(this.name)
                .timestamp(dateTime.now())
                .build();
    }
}
