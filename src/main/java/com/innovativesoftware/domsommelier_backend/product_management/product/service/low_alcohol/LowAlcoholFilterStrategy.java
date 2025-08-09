package com.innovativesoftware.domsommelier_backend.product_management.product.service.low_alcohol;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.LowAlcoholRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LowAlcoholFilterStrategy implements ProductFilterStrategy {
    private final LowAlcoholRepository lowAlcoholRepository;
    private final LowAlcoholSpecification lowAlcoholSpecification;
    private final ProductMapper productMapper;

    @Override
    public List<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return lowAlcoholRepository.findAll(lowAlcoholSpecification.byFilter(params), pageable)
                .map(lowAlcohol -> productMapper.toCardDto(lowAlcohol.getProduct()))
                .toList();
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.low_alcohol;
    }
}
