package com.innovativesoftware.domsommelier_backend.product_management.product.service.snack;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.Snack;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.snack.SnackCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductCardDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnackCardDtoMapper implements ProductCardDtoMapper {

    private final SnackRepository snackRepository;

    @Override
    public ProductCardDto toCardDto(Product product) {
        Snack snack = snackRepository.findById(product.getId()).orElseThrow(
                () -> new IllegalStateException("No snack with id: " + product.getId())
        );
        return SnackCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .article(product.getArticle())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .productCountry(product.getProductCountry().getName())
                .productCategoryName(product.getProductCategory().getName().name())
                .productPhoto(product.getProductPhoto().stream().map(photo -> FileDTO.builder()
                        .id(photo.getId())
                        .bucket(photo.getBucket())
                        .name(photo.getName())
                        .description(photo.getDescription())
                        .build()).toList())
                .category(SnackCardDto.Category.valueOf(snack.getCategory().getName().name()))
                .build();
    }

    @Override
    public String getSupportedCategory() {
        return ProductCategoryEnum.snack.name();
    }
}
