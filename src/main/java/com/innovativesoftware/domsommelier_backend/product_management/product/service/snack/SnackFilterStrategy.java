package com.innovativesoftware.domsommelier_backend.product_management.product.service.snack;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SnackFilterStrategy implements ProductFilterStrategy {

    private final SnackRepository snackRepository;
    private final SnackSpecification snackSpecification;

    @Override
    public List<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return snackRepository.findAll(snackSpecification.byFilter(params), pageable)
                .map(snack -> ProductMapper.toCardDto(snack.getProduct()))
                .toList();
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.snack;
    }
}
