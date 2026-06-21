package com.innovativesoftware.domsommelier_backend.product_management.product.service.accessories;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories.Accessories;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.accessories.AccessoriesCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.AccessoriesRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductCardDtoMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductPhotoUrls;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessoriesCardDtoMapper implements ProductCardDtoMapper {

    private final AccessoriesRepository accessoriesRepository;

    @Override
    public ProductCardDto toCardDto(Product product) {
        Accessories accessories = accessoriesRepository.findById(product.getId()).orElseThrow(
                () -> new IllegalStateException("Accessories with id " + product.getId() + " not found")
        );
        if (accessories == null) return null;
        return AccessoriesCardDto.builder()
                .id(product.getId())
                .name(product.getName())
                .article(product.getArticle())
                .price(product.getPrice())
                .discount(product.getDiscount())
                .productCountry(product.getProductCountry().getName())
                .productCategoryName(product.getProductCategory().getName().name())
                .productPhoto(ProductPhotoUrls.toFileDtos(product))
                .build();
    }

    @Override
    public String getSupportedCategory() {
        return ProductCategoryEnum.accessories.name();
    }
}
