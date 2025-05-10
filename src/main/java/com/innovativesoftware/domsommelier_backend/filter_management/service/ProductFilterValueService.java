package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.ProductFilterValue;
import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterOptionRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.ProductFilterValueRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.util.FilterMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductFilterValueService {
    private final ProductFilterValueRepository repository;
    private final ProductRepository productRepository;
    private final FilterRepository filterRepository;
    private final FilterOptionRepository filterOptionRepository;

    public List<ProductFilterValueDtoResponse> getAll() {
        log.info("Getting all product filter values.");
        return repository.findAll().stream().map(FilterMapper::toDTO).toList();
    }

    public List<ProductFilterValueDtoResponse> getByProductId(UUID productId) {
        log.info("Getting all product filter values for productId={}", productId);
        return repository.findAllByProductId(productId).stream().map(FilterMapper::toDTO).toList();
    }

    public ProductFilterValueDtoResponse getById(UUID id) {
        log.info("Getting product filter value with id={}", id);
        return FilterMapper.toDTO(repository.findById(id).orElseThrow());
    }

    public ProductFilterValueDtoResponse create(ProductFilterValueDtoRequest dto) {
        log.info("Creating product filter value.");
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();
        FilterOption option = filterOptionRepository.findById(dto.getFilterOptionId()).orElseThrow();

        ProductFilterValue entity = ProductFilterValue.builder()
                .product(product)
                .filter(filter)
                .option(option)
                .build();

        ProductFilterValue saved = repository.save(entity);
        log.info("Product filter value created with id={}", saved.getId());
        return FilterMapper.toDTO(saved);
    }

    public UUID delete(UUID id) {
        repository.deleteById(id);
        log.info("Product filter value with id={} deleted.", id);
        return id;
    }

    public List<UUID> getAllByFilterIdAndFilterOptionId(UUID filterId, UUID filterOptionId) {
        return repository.findAllByFilterIdAndOptionId(filterId, filterOptionId)
                .stream().map(ProductFilterValue::getId).toList();
    }

    public ProductFilterValueDtoResponse update(UUID id, ProductFilterValueDtoRequest dto) {
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();
        FilterOption option = filterOptionRepository.findById(dto.getFilterOptionId()).orElseThrow();

        ProductFilterValue entity = ProductFilterValue.builder()
                .id(id)
                .product(product)
                .filter(filter)
                .option(option)
                .build();

        ProductFilterValue saved = repository.save(entity);
        log.info("Product filter value updated with id={}", saved.getId());
        return FilterMapper.toDTO(saved);
    }
}
