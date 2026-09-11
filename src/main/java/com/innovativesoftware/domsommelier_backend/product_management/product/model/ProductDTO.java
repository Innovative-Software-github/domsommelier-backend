package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderItemDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.model.StorageHistoryDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductDTO {
    private UUID id;
    private String article;
    private String name;
    private BigDecimal initialPrice;
    private BigDecimal price;
    private String description;
    private String aroma;
    private String taste;
    private String foodPairing;
    /** Акционная цена для всех покупателей, {@code null} — акции нет. */
    private BigDecimal salePrice;
    private OffsetDateTime createdAt;
    private String productCountry;
    private ProductCategoryEnum productCategoryName;
    private List<FileDTO> productPhoto;
    private List<OrderItemDto> orderItems;
    private List<StorageHistoryDto> storageHistories;
    private Object details;

    /** @deprecated старое имя поля {@code salePrice}, см. {@link ProductCardDto#getDiscount()}. */
    @Deprecated
    public BigDecimal getDiscount() {
        return salePrice;
    }
}
