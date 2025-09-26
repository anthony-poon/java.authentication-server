package com.anthonypoon.authenticationserver.client.mailgun.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Builder
public class SendEmailRequest {
    private String from;
    private String to;
    private String subject;
    private String html;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<MultipartFile> attachment;
}

