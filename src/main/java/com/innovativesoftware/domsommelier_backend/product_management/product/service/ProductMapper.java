package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderItemDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.model.StorageHistoryDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductMapper {

    public static ProductCardDto toCardDto(Product product) {
       switch (product.getProductCategory().getName()) {
           case WINE:
       }
    }

    /*public static ProductCardDto toCardDto(Product product) {
        return ProductCardDto.builder()
                .id(product.getId()).name(product.getName()).article(product.getArticle())
                .productCategoryName(String.valueOf(product.getProductCategory().getName()))
                .discount(product.getDiscount()).price(product.getPrice()).productPhoto(
                        product.getProductPhoto().stream().map(photo -> FileDTO.builder()
                                .id(photo.getId())
                                .bucket(photo.getBucket())
                                .name(photo.getName())
                                .description(photo.getDescription())
                                .build()).toList()
                )
                .productCountry(product.getProductCountry().getName()).build();
    }*/

    public static ProductDTO toProductDto(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .article(product.getArticle())
                .name(product.getName())
                .initialPrice(product.getInitialPrice())
                .price(product.getPrice())
                .description(product.getDescription())
                .discount(product.getDiscount())
                .createdAt(product.getCreatedAt())
                .productCountry(product.getProductCountry().getName())
                .productCategoryName(String.valueOf(product.getProductCategory().getName()))
                .productPhoto(product.getProductPhoto().stream().map(photo -> FileDTO.builder()
                        .id(photo.getId())
                        .bucket(photo.getBucket())
                        .name(photo.getName())
                        .description(photo.getDescription())
                        .build()).toList())
                .orderItems(product.getOrderItems().stream().map(orderItem -> OrderItemDto.builder()
                        .id(orderItem.getId())
                        .quantity(orderItem.getQuantity())
                        .order(orderItem.getOrder().getId())
                        .build()).toList())
                .storageHistories(product.getProducts().stream().map(storageHistory -> StorageHistoryDto.builder()
                        .id(storageHistory.getId())
                        .amount(storageHistory.getAmount())
                        .createdAt(storageHistory.getCreatedAt())
                        .build()).toList())
                .build();
    }
}
