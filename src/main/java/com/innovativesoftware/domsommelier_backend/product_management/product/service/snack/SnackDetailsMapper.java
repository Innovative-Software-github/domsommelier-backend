package com.innovativesoftware.domsommelier_backend.product_management.product.service.snack;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.Snack;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.snack.SnackDetailsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnackDetailsMapper implements ProductDetailsMapper<SnackDetailsDto> {

    private final SnackRepository snackRepository;

    @Override
    public boolean supports(ProductCategoryEnum category) {
        return category == ProductCategoryEnum.snack;
    }

    @Override
    public SnackDetailsDto mapDetails(Product product) {
        Snack snack = snackRepository.findById(product.getId()).orElse(null);
        if (snack == null) return null;
        return SnackDetailsDto.builder()
                .producer(snack.getProducer())
                .pairings(snack.getPairings())
                .category(String.valueOf(snack.getCategory().getName()))
                .build();
    }
}
