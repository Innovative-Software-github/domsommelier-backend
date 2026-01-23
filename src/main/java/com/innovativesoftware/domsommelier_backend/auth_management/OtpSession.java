package com.innovativesoftware.domsommelier_backend.auth_management;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtpSession {

    private String code;
    private LocalDateTime  lastSentTime;

    public OtpSession(String code, LocalDateTime lastSentTime) {
        this.code = code;
        this.lastSentTime = lastSentTime;
    }
}