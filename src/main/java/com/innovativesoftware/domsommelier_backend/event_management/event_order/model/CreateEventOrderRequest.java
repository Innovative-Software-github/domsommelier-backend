package com.innovativesoftware.domsommelier_backend.event_management.event_order.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateEventOrderRequest {

    @NotBlank(message = "Имя обязательно")
    @Size(max = 255, message = "Имя слишком длинное")
    private String name;

    @NotBlank(message = "Номер телефона обязателен")
    @Size(max = 50, message = "Номер телефона слишком длинный")
    private String phone;

    @Size(max = 2000, message = "Комментарий слишком длинный")
    private String comment;
}
