package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
public abstract class ProductCardDto {
    private UUID id;
    private String article;
    private String name;
    /** Цена по прайсу. При наличии {@link #salePrice} показывается зачёркнутой. */
    private BigDecimal price;
    /** Акционная цена для всех покупателей, {@code null} — акции нет. */
    private BigDecimal salePrice;
    private String productCountry;
    private String productCategoryName;
    private List<FileDTO> productPhoto;

    /**
     * @deprecated старое имя поля {@code salePrice}. Оставлено на один релиз, чтобы не сломать
     * фронт, задеплоенный до бэкенда. Удалить вместе с {@code @JsonAlias} в ProductWriteRequest.
     */
    @Deprecated
    public BigDecimal getDiscount() {
        return salePrice;
    }
}
