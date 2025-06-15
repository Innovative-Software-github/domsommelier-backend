package com.innovativesoftware.domsommelier_backend.product_management.product.service.accessories;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.AccessoriesRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccessoriesFilterStrategy implements ProductFilterStrategy {

    private final AccessoriesRepository accessoriesRepository;
    private final AccessoriesSpecification accessoriesSpecification;

    @Override
    public List<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return accessoriesRepository.findAll(accessoriesSpecification.byFilter(params), pageable)
                .map(accessories -> ProductMapper.toCardDto(accessories.getProduct()))
                .toList();
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.accessories;
    }
}
