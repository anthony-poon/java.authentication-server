package com.anthonypoon.authenticationserver.service.email.mail;


import com.anthonypoon.authenticationserver.service.email.config.EmailConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Getter
@SuperBuilder
public class AccountConfirmationMail extends Mail {
    private final static String URL_PATH = "registration/validation";
    private final static String TEMPLATE_PATH = "emails/account-confirmation.html";

    private final String tokenValue;

    @Override
    public String getTemplatePath() {
        return TEMPLATE_PATH;
    }

    @Override
    public Map<String, Object> build(EmailConfig config) {
        var url = config.getBaseUrl() + "/" + URL_PATH + "/" + tokenValue;
        return Map.of("context",
                Context.builder()
                        .url(url)
                        .build()
        );
    }

    @Getter
    @Builder
    public static class Context {
        private String url;
    }
}
