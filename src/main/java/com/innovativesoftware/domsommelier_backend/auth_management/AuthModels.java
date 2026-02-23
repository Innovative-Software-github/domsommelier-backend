package com.innovativesoftware.domsommelier_backend.auth_management;

import lombok.Builder;
import lombok.Data;

public class AuthModels {

    @Data
    public static class AuthInitiateRequest {
        private String email;
    }

    @Data
    public static class AuthInitiateResponse {
        private String success;
    }

    @Data
    public static class AuthConfirmRequest {
        private String email;
        private String code;
    }

    @Data
    @Builder
    public static class AuthResponse {
        private String token;
        private String customerId;
        private String firstName;
        private String secondName;
    }
}
