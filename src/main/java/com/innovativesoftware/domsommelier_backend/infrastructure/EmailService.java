package com.innovativesoftware.domsommelier_backend.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class EmailService {

    @Value("${spring.mail.from}")
    private String fromAddress;

    @Value("${app.email.subject}")
    private String emailSubject;

    @Bean
    public SimpleMailMessage templateMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setSubject(emailSubject);
        return message;
    }
}