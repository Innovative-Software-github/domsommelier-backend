package com.innovativesoftware.domsommelier_backend.product_test.model;

import com.innovativesoftware.domsommelier_backend.product_test.enums.ProductTypes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "product_test")
public class ProductTest {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "type")
    @Enumerated(EnumType.ORDINAL)
    private ProductTypes type;

    @Column(name = "price")
    private Integer price;
}