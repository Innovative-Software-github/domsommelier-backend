package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.spirit.SpiritDetailsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductDetailsMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.VolumeStrengthUtils;
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
                .volume(VolumeStrengthUtils.formatVolume(spirit.getVolume()))
                .category(spirit.getCategory().getName())
                .strength(VolumeStrengthUtils.formatStrength(spirit.getStrength()))
                .producer(spirit.getProducer())
                .features(spirit.getFeatures())
                .build();
    }
}