package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.spirit.SpiritDetailsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpiritDetailsMapper implements ProductDetailsMapper<SpiritDetailsDto> {
    private final SpiritRepository spiritRepository;

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.spirit;
    }

    @Override
    public SpiritDetailsDto mapDetails(Product product) {
        Spirit spirit = spiritRepository.findById(product.getId()).orElse(null);
        if (spirit == null) return null;
        return SpiritDetailsDto.builder()
                .volume(spirit.getVolume().getName().name())
                .category(spirit.getCategory().getName())
                .strength(spirit.getStrength().getName().name())
                .producer(spirit.getProducer())
                .features(spirit.getFeatures())
                .build();
    }
}