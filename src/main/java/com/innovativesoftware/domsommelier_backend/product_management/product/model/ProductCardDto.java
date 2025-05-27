package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
public abstract class ProductCardDto {
    private UUID id;
    private String article;
    private String name;
    private Integer price;
    private Integer discount;
    private String productCountry;
    private String productCategoryName;
    private List<FileDTO> productPhoto;

}
