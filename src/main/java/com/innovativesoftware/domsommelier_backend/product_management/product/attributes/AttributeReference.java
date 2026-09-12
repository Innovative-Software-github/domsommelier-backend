package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "catalog_attribute_reference", uniqueConstraints = @UniqueConstraint(columnNames = {"kind", "code"}))
@Getter @Setter
public class AttributeReference {
    @Id @Column(length = 140) private String id;
    @Column(nullable = false, length = 50) private String kind;
    @Column(nullable = false, length = 80) private String code;
    @Column(nullable = false, length = 160) private String label;
}
