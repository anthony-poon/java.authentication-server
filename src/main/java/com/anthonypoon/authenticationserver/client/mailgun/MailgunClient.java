package com.anthonypoon.authenticationserver.client.mailgun;

import com.anthonypoon.authenticationserver.client.mailgun.request.SendEmailRequest;
import com.anthonypoon.authenticationserver.client.mailgun.request.SendEmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(
        name = "Mailgun",
        configuration = MailgunConfig.class,
        url = "${application.http-client.mailgun.url}"
)
public interface MailgunClient {
    @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    SendEmailResponse sendMail(SendEmailRequest request);
}
