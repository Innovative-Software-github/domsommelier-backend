package com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "snack_category")
public class SnackCategory {
    public enum Category {Cheese, Jamon, Bresaola, Assorted}

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false)
    private Category name;

    @OneToMany(mappedBy = "category")
    private List<Snack> snacks;
}
