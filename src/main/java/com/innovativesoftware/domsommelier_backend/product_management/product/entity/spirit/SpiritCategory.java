package com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "spirit_category")
public class SpiritCategory {
    //public enum Category { Whiskey, Cognac, Vodka, Tequila, Rum, }

//    @Id
//    @Enumerated(EnumType.STRING)
//    @Column(name = "name", nullable = false)
//    private Category name;
    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Spirit> spirits;
}
