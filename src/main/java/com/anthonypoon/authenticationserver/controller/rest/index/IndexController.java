package com.anthonypoon.authenticationserver.controller.rest.index;

import com.anthonypoon.authenticationserver.controller.rest.index.response.HeartbeatResponse;
import com.anthonypoon.authenticationserver.controller.rest.index.usecase.HeartbeatUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class IndexController {
    public final HeartbeatUseCase heartbeat;

    public IndexController(HeartbeatUseCase heartbeat) {
        this.heartbeat = heartbeat;
    }



    @GetMapping("/heartbeat")
    public HeartbeatResponse getHeartbeat() {
        return this.heartbeat.getHeartbeat();
    }
}
