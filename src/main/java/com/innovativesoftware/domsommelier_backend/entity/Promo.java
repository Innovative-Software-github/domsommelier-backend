package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "promo")
public class Promo {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "discount", nullable = false)
    private Integer discount;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
}