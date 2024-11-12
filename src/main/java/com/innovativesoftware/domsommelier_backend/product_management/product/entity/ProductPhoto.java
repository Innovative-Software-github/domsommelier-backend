package com.innovativesoftware.domsommelier_backend.product_management.product.entity;

import com.innovativesoftware.domsommelier_backend.file_management.model.File;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "product_photo")
public class ProductPhoto extends File<Product> {

}