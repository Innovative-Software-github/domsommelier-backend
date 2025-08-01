package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.spirit.SpiritCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductCardDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpiritCardDtoMapper implements ProductCardDtoMapper {

    private final SpiritRepository spiritRepository;

    @Override
    public ProductCardDto toCardDto(Product product) {
        Spirit spirit = spiritRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Spirit not found for product " + product.getId()));
        return SpiritCardDto.builder()
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
                .volume(SpiritCardDto.Volume.valueOf(spirit.getVolume().getName()))
                .category(spirit.getCategory().getName())
                .strength(SpiritCardDto.Strength.valueOf(spirit.getStrength().getName()))
                .build();
    }

    @Override
    public String getSupportedCategory() {
        return ProductCategoryEnum.spirit.name();
    }
}
