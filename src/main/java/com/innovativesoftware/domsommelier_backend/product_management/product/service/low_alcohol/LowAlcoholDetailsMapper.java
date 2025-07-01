package com.innovativesoftware.domsommelier_backend.product_management.product.service.low_alcohol;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcohol;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.low_alcohol.LowAlcoholDetailsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.LowAlcoholRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LowAlcoholDetailsMapper implements ProductDetailsMapper<LowAlcoholDetailsDto> {

    private final LowAlcoholRepository lowAlcoholRepository;

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.low_alcohol;
    }

    @Override
    public Object mapDetails(Product product) {
        LowAlcohol lowAlcohol = lowAlcoholRepository.findById(product.getId()).orElseThrow(
                () -> new IllegalStateException("No low alcohol with id " + product.getId())
        );
        if (lowAlcohol == null) return null;
        return LowAlcoholDetailsDto.builder()
                .producer(lowAlcohol.getProducer())
                .strength(lowAlcohol.getStrength().getName().name())
                .volume(lowAlcohol.getVolume().getName().name())
                .category(lowAlcohol.getCategory().getName().name())
                .features(lowAlcohol.getFeatures())
                .build();
    }
}
