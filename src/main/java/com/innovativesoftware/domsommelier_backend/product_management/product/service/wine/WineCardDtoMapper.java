package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.wine.WineCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductCardDtoMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductPhotoUrls;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.VolumeStrengthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WineCardDtoMapper implements ProductCardDtoMapper {

    private final WineRepository wineRepository;

    @Override
    public ProductCardDto toCardDto(Product product) {
        Wine wine = wineRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Wine not found for product " + product.getId()));
        return WineCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .article(product.getArticle())
                .price(product.getPrice())
                .salePrice(product.getSalePrice())
                .productCountry(product.getProductCountry().getName())
                .productCategoryName(product.getProductCategory().getName().name())
                .productPhoto(ProductPhotoUrls.toFileDtos(product))
                .volume(VolumeStrengthUtils.formatVolume(wine.getVolume()))
                .color(wine.getColor() != null ? wine.getColor().getName() : null)
                .type(wine.getType() != null ? wine.getType().getName() : null)
                .build();
    }

    @Override
    public String getSupportedCategory() {
        return "wine";
    }
}
