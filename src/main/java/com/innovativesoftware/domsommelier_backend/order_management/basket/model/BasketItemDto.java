package com.innovativesoftware.domsommelier_backend.order_management.basket.model;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketItemDto implements Serializable {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasketItemDtoIdClass {
        private UUID id;
        private String article;
        private String name;
        private BigDecimal price;
        private Integer discount;
        private String productCountry;
        private String productCategoryName;
        private List<FileDTO> productPhoto;
    }
    private BasketItemDtoIdClass product;
    private Integer quantity;
}