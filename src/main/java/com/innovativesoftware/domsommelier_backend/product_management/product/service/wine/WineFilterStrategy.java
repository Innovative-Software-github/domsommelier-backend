package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductFilterStrategy;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("WINE")
@RequiredArgsConstructor
public class WineFilterStrategy implements ProductFilterStrategy {
    private final WineRepository wineRepository;

    @Override
    public List<ProductCardDto> filter(Map<String, Object> params, Pageable pageable) {
        return wineRepository.findAll(WineSpecification.byFilter(params), pageable)
                .map(wine -> ProductMapper.toCardDto(wine.getProduct()))
                .toList();
    }
}
