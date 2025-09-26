package com.anthonypoon.authenticationserver.service.email.mail;

import com.anthonypoon.authenticationserver.service.email.config.EmailConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SuperBuilder
@Getter
abstract public class Mail {
    private String subject;
    private Set<String> recipients;
    private List<MultipartFile> attachments;
    public abstract String getTemplatePath();
    public abstract Map<String, Object> build(EmailConfig config);
}
