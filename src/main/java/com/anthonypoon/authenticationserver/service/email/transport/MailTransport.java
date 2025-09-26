package com.anthonypoon.authenticationserver.service.email.transport;

import com.anthonypoon.authenticationserver.service.email.mail.Mail;

import java.util.Set;

public interface MailTransport {
    // TODO: Implement a sendAll
    void send(Set<String> recipients, Mail mail, String content);
}
