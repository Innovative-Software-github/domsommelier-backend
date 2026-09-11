package com.innovativesoftware.domsommelier_backend.product_management.product.service.snack;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.Snack;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductFacetsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.FacetCounter;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SnackFilterStrategy implements ProductFilterStrategy {

    private final SnackRepository snackRepository;
    private final SnackSpecification snackSpecification;
    private final ProductMapper productMapper;
    private final FacetCounter facetCounter;

    @Override
    public Page<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return snackRepository.findAll(snackSpecification.byFilter(params), pageable)
                .map(snack -> productMapper.toCardDto(snack.getProduct()));
    }

    @Override
    public ProductFacetsDto facets(Map<String, Object> params, Collection<String> fields) {
        return facetCounter.count(Snack.class, snackSpecification, params, fields);
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.snack;
    }
}
