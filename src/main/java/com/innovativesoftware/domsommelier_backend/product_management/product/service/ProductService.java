package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.google.gson.Gson;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderItemDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.model.StorageHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final Gson gson;
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private ProductCountryRepository productCountryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public List<ProductCountryProjection> getCountriesWithWines() {
        return productCountryRepository.getCountriesWithProductCategory(ProductCategories.WINE);
    }

    public List<ProductCategoryProjection> getProductCategories() {
        return productCategoryRepository.findAllCategories();
    }

    public String searchProductsByName(String name) {
        List<UUID> products = productRepository.findByNameContainingIgnoreCase(name);
        return gson.toJson(products);
    }

    public ProductDTO getProductDetails(UUID productId) {
        var product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
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

    public List<UUID> getAllProducts() {
        return productRepository.findAll().stream().map(Product::getId).toList();
    }

    public List<UUID> getAllProductsByCategory(String productCategory) {
        return productRepository.findByProductCategory(ProductCategories.valueOf(productCategory));
    }

    public List<UUID> getAllProductsByCountry(String country) {
        return productRepository.findByProductCountry(country.toLowerCase());
    }
}
