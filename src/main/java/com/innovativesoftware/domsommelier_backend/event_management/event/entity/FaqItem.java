package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Один вопрос-ответ в FAQ мероприятия. Хранится как element collection на
 * {@link Event} (без собственного id — заменяется целиком при сохранении,
 * как grapes/features у Wine).
 */
@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class FaqItem {

    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;
}
