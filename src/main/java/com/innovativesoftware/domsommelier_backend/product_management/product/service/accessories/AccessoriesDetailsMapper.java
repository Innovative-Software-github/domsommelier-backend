package com.innovativesoftware.domsommelier_backend.product_management.product.service.accessories;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories.Accessories;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.accessories.AccessoriesDetailsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.AccessoriesRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessoriesDetailsMapper implements ProductDetailsMapper<AccessoriesDetailsDto> {

    private final AccessoriesRepository accessoriesRepository;

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.accessories;
    }

    @Override
    public Object mapDetails(Product product) {
        Accessories accessories = accessoriesRepository.findById(product.getId()).orElseThrow(
                () -> new IllegalStateException("Accessories with id " + product.getId() + " not found")
        );
        return AccessoriesDetailsDto.builder()
                .producer(accessories.getProducer())
                .features(accessories.getFeatures())
                .build();
    }
}
