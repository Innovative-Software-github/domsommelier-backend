package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderItemDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductPhotoUrls;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.model.StorageHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ProductCardDtoMapperRegistry cardDtoMapperRegistry;
    private final ProductDetailsMapperRegistry registry;

    public ProductCardDto toCardDto(Product product) {
        String categoryName = product.getProductCategory().getName().name();
        ProductCardDtoMapper cardDtoMapper = cardDtoMapperRegistry.getMapper(categoryName);
        return cardDtoMapper.toCardDto(product);
    }

    public ProductDTO toProductDto(Product product) {
        ProductDTO.ProductDTOBuilder builder = ProductDTO.builder()
                .id(product.getId())
                .article(product.getArticle())
                .name(product.getName())
                .initialPrice(product.getInitialPrice())
                .price(product.getPrice())
                .description(product.getDescription())
                .aroma(product.getAroma())
                .taste(product.getTaste())
                .foodPairing(product.getFoodPairing())
                .salePrice(product.getSalePrice())
                .createdAt(product.getCreatedAt())
                .productCountry(product.getProductCountry().getName())
                .productCategoryName(product.getProductCategory().getName())
                .productPhoto(ProductPhotoUrls.toFileDtos(product))
                .orderItems(product.getOrderItems().stream().map(orderItem -> OrderItemDto.builder()
                        .id(orderItem.getId())
                        .quantity(orderItem.getQuantity())
                        .order(orderItem.getOrder().getId())
                        .build()).toList())
                .storageHistories(product.getProducts().stream().map(storageHistory -> StorageHistoryDto.builder()
                        .id(storageHistory.getId())
                        .amount(storageHistory.getAmount())
                        .createdAt(storageHistory.getCreatedAt())
                        .build()).toList());

        registry.findMapper(product.getProductCategory().getName())
                .ifPresent(mapper -> builder.details(mapper.mapDetails(product)));

        return builder.build();
    }
}
