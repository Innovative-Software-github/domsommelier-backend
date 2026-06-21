package com.innovativesoftware.domsommelier_backend.product_management.product.service.low_alcohol;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcohol;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.low_alcohol.LowAlcoholCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.LowAlcoholRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductCardDtoMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductPhotoUrls;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.VolumeStrengthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowAlcoholCardDtoMapper implements ProductCardDtoMapper {

    private final LowAlcoholRepository lowAlcoholRepository;

    @Override
    public ProductCardDto toCardDto(Product product) {
        LowAlcohol lowAlcohol = lowAlcoholRepository.findById(product.getId()).orElseThrow(
                () -> new IllegalStateException("Could not find Low Alcohol with id " + product.getId())
        );
        if (lowAlcohol == null) return null;
        return LowAlcoholCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .article(product.getArticle())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .productCountry(product.getProductCountry().getName())
                .productCategoryName(product.getProductCategory().getName().name())
                .productPhoto(ProductPhotoUrls.toFileDtos(product))
                .subcategory(lowAlcohol.getCategory().getName())
                .volume(VolumeStrengthUtils.formatVolume(lowAlcohol.getVolume()))
                .build();
    }

    @Override
    public String getSupportedCategory() {
        return ProductCategoryEnum.low_alcohol.name();
    }
}
