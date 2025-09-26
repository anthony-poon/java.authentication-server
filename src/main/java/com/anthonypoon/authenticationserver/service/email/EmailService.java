package com.anthonypoon.authenticationserver.service.email;

import com.anthonypoon.authenticationserver.service.email.config.EmailConfig;
import com.anthonypoon.authenticationserver.service.email.mail.Mail;
import com.anthonypoon.authenticationserver.service.email.transport.MailTransport;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import java.util.List;
import java.util.Set;

@Service
public class EmailService {
    private final EmailConfig config;
    private final TemplateEngine engine;
    private final MailTransport transport;

    public EmailService(EmailConfig config, TemplateEngine engine, MailTransport transport) {
        this.config = config;
        this.engine = engine;
        this.transport = transport;
    }

    // TODO: Implement queuing
    public void send(Mail mail) {
        var mapping = mail.build(config);
        var context = new Context();
        for (var entry: mapping.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }
        var html = this.engine.process(mail.getTemplatePath(), context);
        this.transport.send(mail.getRecipients(), mail, html);
    }
}
