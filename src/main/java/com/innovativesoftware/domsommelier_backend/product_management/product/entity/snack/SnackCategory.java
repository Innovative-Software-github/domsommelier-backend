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

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "subcategory")
    private List<Snack> snacks;
}
