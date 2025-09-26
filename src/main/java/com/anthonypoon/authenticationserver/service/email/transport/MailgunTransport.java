package com.anthonypoon.authenticationserver.service.email.transport;

import com.anthonypoon.authenticationserver.client.mailgun.MailgunClient;
import com.anthonypoon.authenticationserver.client.mailgun.request.SendEmailRequest;
import com.anthonypoon.authenticationserver.service.email.config.EmailConfig;
import com.anthonypoon.authenticationserver.service.email.mail.Mail;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MailgunTransport implements MailTransport {
    private final EmailConfig config;
    private final MailgunClient client;

    public MailgunTransport(EmailConfig config, MailgunClient client) {
        this.config = config;
        this.client = client;
    }

    @Override
    public void send(Set<String> recipients, Mail mail, String content) {
        var request = SendEmailRequest.builder()
                .to(String.join(",", recipients))
                .from(config.getFrom())
                .html(content)
                .subject(mail.getSubject())
                .attachment(mail.getAttachments())
                .build();
        this.client.sendMail(request);
    }
}
