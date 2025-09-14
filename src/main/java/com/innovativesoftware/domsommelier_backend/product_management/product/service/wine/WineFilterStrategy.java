package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WineFilterStrategy implements ProductFilterStrategy {
    private final WineRepository wineRepository;
    private final WineSpecification wineSpecification;
    private final ProductMapper productMapper;

    @Override
    public List<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return wineRepository.findAll(wineSpecification.byFilter(params), pageable)
                .map(wine -> productMapper.toCardDto(wine.getProduct()))
                .toList();
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.wine;
    }
}
