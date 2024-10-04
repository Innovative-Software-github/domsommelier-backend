package com.innovativesoftware.domsommelier_backend.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "wine_color")
public class WineColor {
    enum Color {RED, WHITE, PINK;}

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Color name;

    @OneToMany(mappedBy = "color")
    private List<Wine> wines;

}