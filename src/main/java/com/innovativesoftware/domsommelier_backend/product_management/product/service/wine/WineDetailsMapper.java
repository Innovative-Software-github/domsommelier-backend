package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.wine.WineDetailsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductDetailsMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.RussianLabelTranslator;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.VolumeStrengthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineDetailsMapper implements ProductDetailsMapper<WineDetailsDto> {
    private final WineRepository wineRepository;

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.wine;
    }

    @Override
    public WineDetailsDto mapDetails(Product product) {
        Wine wine = wineRepository.findById(product.getId()).orElse(null);
        if (wine == null) return null;
        return WineDetailsDto.builder()
                .productionYear(wine.getProductionYear())
                .color(wine.getColor() != null ? wine.getColor().getName() : null)
                .type(wine.getType() != null ? wine.getType().getName() : null)
                .grapes(wine.getExtendedDetails() != null && wine.getExtendedDetails().grapeComposition() != null
                    ? wine.getExtendedDetails().grapeComposition().stream().map(share -> share.grape().label()).toList()
                    : RussianLabelTranslator.translateGrapes(wine.getGrapes()))
                .producer(wine.getProducer())
                .volume(VolumeStrengthUtils.formatVolume(wine.getVolume()))
                .features(RussianLabelTranslator.translateFeatures(wine.getFeatures()))
                .extendedDetails(wine.getExtendedDetails())
                .build();
    }
}