package com.innovativesoftware.domsommelier_backend.saved_management.model;

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
public class SavedItemDto implements Serializable {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavedProductDto implements Serializable {
        private UUID id;
        private String article;
        private String name;
        private BigDecimal price;
        /** Акционная цена для всех покупателей, {@code null} — акции нет. */
        private BigDecimal salePrice;
        private String productCountry;
        private String productCategoryName;
        private List<FileDTO> productPhoto;

        /** @deprecated старое имя поля {@code salePrice}. Оставлено на один релиз. */
        @Deprecated
        public BigDecimal getDiscount() {
            return salePrice;
        }
    }

    private SavedProductDto product;
}
