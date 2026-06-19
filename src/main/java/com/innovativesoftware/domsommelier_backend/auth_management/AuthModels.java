package com.innovativesoftware.domsommelier_backend.auth_management;

import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

public class AuthModels {

    @Data
    public static class AuthInitiateRequest {
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Некорректный формат email")
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
        private Role role;
    }
}
