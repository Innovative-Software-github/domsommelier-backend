package com.innovativesoftware.domsommelier_backend.product_management.product.service.accessories;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories.Accessories;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductFacetsDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.AccessoriesRepository;
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
public class AccessoriesFilterStrategy implements ProductFilterStrategy {

    private final AccessoriesRepository accessoriesRepository;
    private final AccessoriesSpecification accessoriesSpecification;
    private final ProductMapper productMapper;
    private final FacetCounter facetCounter;

    @Override
    public Page<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return accessoriesRepository.findAll(accessoriesSpecification.byFilter(params), pageable)
                .map(accessories -> productMapper.toCardDto(accessories.getProduct()));
    }

    @Override
    public ProductFacetsDto facets(Map<String, Object> params, Collection<String> fields) {
        return facetCounter.count(Accessories.class, accessoriesSpecification, params, fields);
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.accessories;
    }
}
