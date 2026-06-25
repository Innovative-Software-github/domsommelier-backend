package com.innovativesoftware.domsommelier_backend.news_management.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Тело создания/обновления новости (без обложки — она грузится отдельным эндпоинтом). */
@Data
public class NewsRequest {

    @NotBlank(message = "Укажите заголовок")
    @Size(max = 255, message = "Заголовок не длиннее 255 символов")
    private String title;

    @Size(max = 4000, message = "Текст не длиннее 4000 символов")
    private String description;

    @Size(max = 2048, message = "Ссылка не длиннее 2048 символов")
    private String reference;
}
