package com.innovativesoftware.domsommelier_backend.product_management.product.service.shampaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.shampaigne_sparkling.ShampaigneAndSparklingCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductCardDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShampaigneSparklingCardDtoMapper implements ProductCardDtoMapper {

    private final SparklingWineRepository sparklingWineRepository;

    @Override
    public ProductCardDto toCardDto(Product product) {
        SparklingWine sparklingWine = sparklingWineRepository.findById(product.getId()).orElseThrow(
                () -> new RuntimeException("No sparkling wine found for id: " + product.getId())
        );
        return ShampaigneAndSparklingCardDto.builder()
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
                .color(ShampaigneAndSparklingCardDto.Color.valueOf(sparklingWine.getColor().getName()))
                .category(ShampaigneAndSparklingCardDto.Category.valueOf(sparklingWine.getCategory().getName()))
                .content(ShampaigneAndSparklingCardDto.Content.valueOf(sparklingWine.getContent().getName()))
                .volume(ShampaigneAndSparklingCardDto.Volume.valueOf(sparklingWine.getVolume().getName()))
                .build();
    }

    @Override
    public String getSupportedCategory() {
        return ProductCategoryEnum.champagne_and_sparkling.name();
    }
}
