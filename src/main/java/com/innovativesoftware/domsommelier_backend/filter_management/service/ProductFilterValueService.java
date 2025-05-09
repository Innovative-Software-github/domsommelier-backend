package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.ProductFilterValue;
import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.ProductFilterValueRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.util.FilterMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductFilterValueService {
    private final ProductFilterValueRepository repository;
    private final ProductRepository productRepository;
    private final FilterRepository filterRepository;

    public List<ProductFilterValueDtoResponse> getAll() {
        return repository.findAll().stream().map(FilterMapper::toDTO).toList();
    }

    public List<ProductFilterValueDtoResponse> getByProductId(UUID productId) {
        return repository.findAllByProductId(productId).stream().map(FilterMapper::toDTO).toList();
    }

    public ProductFilterValueDtoResponse getById(UUID id) {
        return FilterMapper.toDTO(repository.findById(id).orElseThrow());
    }

    public ProductFilterValueDtoResponse create(ProductFilterValueDtoRequest dto) {
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();

        ProductFilterValue entity = ProductFilterValue.builder()
                .product(product)
                .filter(filter)
                .build();
        ProductFilterValue saved = repository.save(entity);
        return FilterMapper.toDTO(saved);
    }

    public UUID delete(UUID id) {
        repository.deleteById(id);
        return id;
    }

    public ProductFilterValueDtoResponse update(UUID id, ProductFilterValueDtoRequest dto) {
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();

        ProductFilterValue entity = ProductFilterValue.builder()
                .id(id)
                .product(product)
                .filter(filter)
                .build();
        ProductFilterValue saved = repository.save(entity);
        return FilterMapper.toDTO(saved);
    }
}
