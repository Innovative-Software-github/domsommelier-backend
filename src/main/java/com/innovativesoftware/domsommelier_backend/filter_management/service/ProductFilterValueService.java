package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.ProductFilterValue;
import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoCreateRequest;
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

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductFilterValueService {
    private final ProductFilterValueRepository productFilterValueRepository;
    private final ProductRepository productRepository;
    private final FilterRepository filterRepository;
    private final FilterOptionRepository filterOptionRepository;

    public List<ProductFilterValueDtoResponse> getAll() {
        log.info("Getting all product filter values.");
        return productFilterValueRepository.findAll().stream().map(FilterMapper::toDTO).toList();
    }

    public List<ProductFilterValueDtoResponse> getByProductId(UUID productId) {
        log.info("Getting all product filter values for productId={}", productId);
        return productFilterValueRepository.findAllByProductId(productId).stream().map(FilterMapper::toDTO).toList();
    }

    public ProductFilterValueDtoResponse getById(UUID id) {
        log.info("Getting product filter value with id={}", id);
        return FilterMapper.toDTO(productFilterValueRepository.findById(id).orElseThrow());
    }

    public ProductFilterValueDtoResponse create(ProductFilterValueDtoCreateRequest dto) {
        log.info("Creating product filter value.");
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();
        FilterOption option = filterOptionRepository.findById(dto.getFilterOptionId()).orElseThrow();

        if (!product.getProductCategory().getName().equals(filter.getProductCategory())) {
            throw new IllegalArgumentException("Product filter value filter and product category mismatch.");
        }

        if (productFilterValueRepository
                .existsByProductIdAndFilterIdAndOption(product.getId(), filter.getId(), option.getId())) {
            throw new IllegalArgumentException("Product filter value already exists.");
        }

        ProductFilterValue entity = ProductFilterValue.builder()
                .product(product)
                .filter(filter)
                .option(option)
                .build();

        ProductFilterValue saved = productFilterValueRepository.save(entity);
        log.info("Product filter value created with id={}", saved.getId());
        return FilterMapper.toDTO(saved);
    }

    public UUID delete(UUID id) {
        productFilterValueRepository.deleteById(id);
        log.info("Product filter value with id={} deleted.", id);
        return id;
    }

    public List<UUID> getAllByFilterIdAndFilterOptionId(Map<String, List<String>> params) {
        List<UUID> result = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            String key = entry.getKey(); // name или field или UUID конкретного фильтра
            List<String> value = entry.getValue(); // значения поля конкретного фильтра
            List<Filter> filter = filterRepository.findByNameOrFieldIgnoreCase(key, key);

            // Если key является UUID фильтра, то просто перезаписываем его
            try {
                UUID uuid = UUID.fromString(key);
                filter = List.of(filterRepository.findById(uuid).orElseThrow(() ->
                        new IllegalArgumentException("Not a valid UUID")));
            } catch (IllegalArgumentException ignored) {}

            // Проверяем все фильтры
            // У нас есть key - название или поле конкретного фильтра и value - значения поля конкретного фильтра
            for (Filter f : filter) {
                Set<UUID> products = new HashSet<>();
                FilterType filterType = f.getType();

                // Если тип фильтра LIST, то в value содержатся значения поля конкретного фильтра
                // Пробегаем по всем значениям поля конкретного фильтра и находим все UUID продуктов,
                // которые содержат эти значения поля конкретного фильтра.
                // Если тип фильтра RANGE, то в value содержатся границы поля конкретного фильтра.
                // Пробегаем по всем значениям поля конкретного фильтра и находим все UUID продуктов,
                // которые содержат эти значения поля конкретного фильтра.
                if (filterType.equals(FilterType.LIST)) {
                    List<UUID> filterOptions = filterOptionRepository.findAllByValues(value).stream()
                            .map(FilterOption::getId).toList();
                    List<Product> products1 = productFilterValueRepository
                            .findAllByFilterOptionIdIn(filterOptions)
                            .stream()
                            .map(ProductFilterValue::getProduct).toList();
                    products.addAll(products1.stream().map(Product::getId).toList());
                } else if (filterType.equals(FilterType.RANGE)) {
                    List<Product> products1 = productFilterValueRepository
                            .findAllByFilterIdAndValueBetween(f.getId(), Float.parseFloat(value.get(0)),
                                    Float.parseFloat(value.get(1)))
                            .stream()
                            .map(ProductFilterValue::getProduct).toList();
                    products.addAll(products1.stream().map(Product::getId).toList());
                } else if (filterType.equals(FilterType.STRING)) {
                    List<Product> products1 = productFilterValueRepository
                            .findAllByFilterIdAndOptionValue(f.getId(), value.get(0))
                            .stream()
                            .map(ProductFilterValue::getProduct).toList();
                    products.addAll(products1.stream().map(Product::getId).toList());
                }
                result.addAll(products);
            }
        }
        return result;
    }

    public ProductFilterValueDtoResponse update(UUID id, ProductFilterValueDtoRequest dto) {
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();
        FilterOption option = filterOptionRepository.findById(dto.getFilterOptionId()).orElseThrow();

        if (!product.getProductCategory().getName().equals(filter.getProductCategory())) {
            throw new IllegalArgumentException("Product filter value filter and product category mismatch.");
        }

        ProductFilterValue entity = ProductFilterValue.builder()
                .id(id)
                .product(product)
                .filter(filter)
                .option(option)
                .build();

        ProductFilterValue saved = productFilterValueRepository.save(entity);
        log.info("Product filter value updated with id={}", saved.getId());
        return FilterMapper.toDTO(saved);
    }
}
