package com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "wine_type")
public class WineType {
    //public enum Type { SWEET, SEMISWEET, DRY, SEMIDRY }

//    @Id
//    @Enumerated(EnumType.STRING)
//    @Column(name = "name", nullable = false)
//    private Type name;

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "type")
    private List<Wine> wines;
}