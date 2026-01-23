package com.innovativesoftware.domsommelier_backend.auth_management;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Контроллер аутентификации"
)
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/initiate")
    @Operation(
            summary = "Логин, сервис запрашивает код с почты"
    )
    public AuthModels.AuthInitiateResponse initiateLogin(AuthModels.AuthInitiateRequest request) {
        AuthModels.AuthInitiateResponse response = new AuthModels.AuthInitiateResponse();
        try {
            authService.initiateLogin(request.getEmail());
            log.info("Код отправлен");
            response.setSuccess("Код отправлен");
            return response;
        } catch (Exception ex) {
            log.error("Ошибка при попытке инициировать логин");
            response.setSuccess(ex.getMessage());
            return response;
        }
    }

    @PostMapping("/confirm")
    @Operation(
            summary = "Подтверждение входа кодом с email"
    )
    public AuthModels.AuthResponse confirmLogin(AuthModels.AuthConfirmRequest request) {
        return authService.confirmLogin(request.getEmail(), request.getCode());
    }
}
