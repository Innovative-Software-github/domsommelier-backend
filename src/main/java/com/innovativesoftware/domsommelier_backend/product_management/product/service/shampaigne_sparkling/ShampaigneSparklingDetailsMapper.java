package com.innovativesoftware.domsommelier_backend.product_management.product.service.shampaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.shampaigne_sparkling.ShampaigneAndSparklingDetailsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductDetailsMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.VolumeStrengthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShampaigneSparklingDetailsMapper implements ProductDetailsMapper<ShampaigneAndSparklingDetailsDto> {

    private final SparklingWineRepository sparklingWineRepository;

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.champagne_and_sparkling;
    }

    @Override
    public Object mapDetails(Product product) {
        SparklingWine sparklingWine = sparklingWineRepository.findById(product.getId()).orElseThrow(
                () -> new IllegalStateException("Sparkling wine not found")
        );
        if (sparklingWine == null) return null;
        return ShampaigneAndSparklingDetailsDto.builder()
                .subcategory(sparklingWine.getSubcategory().getName())
                .content(sparklingWine.getSugarContent().getName())
                .features(sparklingWine.getFeatures())
                .color(sparklingWine.getColor().getName())
                .producer(sparklingWine.getProducer())
                .volume(VolumeStrengthUtils.formatVolume(sparklingWine.getVolume()))
                .build();
    }
}
