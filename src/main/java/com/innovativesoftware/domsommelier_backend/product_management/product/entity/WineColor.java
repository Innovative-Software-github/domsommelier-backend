package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "wine_color")
public class WineColor {
    enum Color {RED, WHITE, PINK;}
    //TODO: enum
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Color name;

    @OneToMany(mappedBy = "color")
    private List<Wine> wines;

}