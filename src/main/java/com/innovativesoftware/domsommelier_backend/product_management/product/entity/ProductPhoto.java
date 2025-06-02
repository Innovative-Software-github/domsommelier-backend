package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "product_photo")
public class ProductPhoto extends File {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}