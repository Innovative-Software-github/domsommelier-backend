package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductFacetsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
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
public class WineFilterStrategy implements ProductFilterStrategy {
    private final WineRepository wineRepository;
    private final WineSpecification wineSpecification;
    private final ProductMapper productMapper;
    private final FacetCounter facetCounter;

    @Override
    public Page<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return wineRepository.findAll(wineSpecification.byFilter(params), pageable)
                .map(wine -> productMapper.toCardDto(wine.getProduct()));
    }

    @Override
    public ProductFacetsDto facets(Map<String, Object> params, Collection<String> fields) {
        return facetCounter.count(Wine.class, wineSpecification, params, fields);
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.wine;
    }
}
