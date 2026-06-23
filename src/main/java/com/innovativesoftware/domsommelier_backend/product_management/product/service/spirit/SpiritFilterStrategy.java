package com.innovativesoftware.domsommelier_backend.product_management.product.service.spirit;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpiritFilterStrategy implements ProductFilterStrategy {
    private final SpiritRepository spiritRepository;
    private final SpiritSpecification spiritSpecification;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return spiritRepository.findAll(spiritSpecification.byFilter(params), pageable)
                .map(spirit -> productMapper.toCardDto(spirit.getProduct()));
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.spirit;
    }
}
