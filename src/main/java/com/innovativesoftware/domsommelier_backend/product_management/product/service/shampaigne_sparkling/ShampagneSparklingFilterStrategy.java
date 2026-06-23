package com.innovativesoftware.domsommelier_backend.product_management.product.service.shampaigne_sparkling;

import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ShampagneSparklingFilterStrategy implements ProductFilterStrategy {
    private final SparklingWineRepository sparklingWineRepository;
    private final ShampaigneSparklingSpecification shampaigneSparklingSpecification;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return sparklingWineRepository.findAll(shampaigneSparklingSpecification.byFilter(params), pageable)
                .map(sparklingWine -> productMapper.toCardDto(sparklingWine.getProduct()));
    }

    @Override
    public ProductCategoryEnum getCategoryEnum() {
        return ProductCategoryEnum.champagne_and_sparkling;
    }
}
