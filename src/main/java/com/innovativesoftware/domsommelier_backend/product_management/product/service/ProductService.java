package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.ProductCountry;
import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterStrategyFactory;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductSearchRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {

    private static final int MAX_SEARCH_PAGE_SIZE = 50;

    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private ProductCountryRepository productCountryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private final ProductFilterStrategyFactory strategyFactory;
    @Autowired
    private final ProductMapper productMapper;
    @Autowired
    private final ProductCardDtoMapperRegistry mapperRegistry;

    @Transactional(readOnly = true)
    public List<ProductCountryProjection> getCountriesWithWines() {
        return productCountryRepository.getCountriesWithProductCategory(ProductCategoryEnum.wine);
    }

    @Transactional(readOnly = true)
    public List<ProductCategoryProjection> getProductCategories() {
        return productCategoryRepository.findAllCategories();
    }

    @Transactional(readOnly = true)
    public Page<ProductCardDto> searchProducts(ProductSearchRequest request) {
        SearchQuery query = SearchQuery.parse(request.getQ());
        if (query.isEmpty()) {
            return Page.empty();
        }

        String city = normalizeCity(request.getCity());
        int page = request.getPage() != null ? Math.max(request.getPage(), 0) : 0;
        int size = capPageSize(request.getSize());

        // Без Sort: порядок (релевантность, затем название) задаёт сама спецификация.
        Pageable pageable = PageRequest.of(page, size);
        Specification<Product> spec = ProductSearchSpecification.byQueryAndCity(query, city);

        return productRepository.findAll(spec, pageable).map(productMapper::toCardDto);
    }

    private static String normalizeCity(String city) {
        if (city == null || city.isBlank()) {
            return null;
        }
        return city.trim().toLowerCase();
    }

    private static int capPageSize(Integer size) {
        if (size == null || size < 1) {
            return 20;
        }
        return Math.min(size, MAX_SEARCH_PAGE_SIZE);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductDetails(UUID productId) {
        var product = productRepository.findById(productId).orElseThrow(() -> new NoSuchElementException("Product not found"));
        return productMapper.toProductDto(product);
    }

    @Transactional(readOnly = true)
    public List<ProductCardDto> getAllProducts(Pageable pageable) {
//        return productRepository.findAll(pageable)
//                .stream()
//                .map(ProductMapper::toCardDto)
//                .toList();

        return productRepository.findAll(pageable)
                .stream()
                .map(product -> {
                    String category = product.getProductCategory().getName().name();
                    return mapperRegistry.getMapper(category).toCardDto(product);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCardDto> getAllProductsByCategory(ProductCategoryEnum productCategory, Pageable pageable) {
        return productRepository.findByProductCategory(productCategory, pageable)
                .stream()
                .map(id -> {
                    var product = productRepository.findById(id).orElseThrow(() ->
                            new RuntimeException("Product not found"));
                    return productMapper.toCardDto(product);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductCardDto> getAllProductsByCountry(String country, Pageable pageable) {
        return productRepository.findByProductCountry(country.toLowerCase(), pageable)
                .stream()
                .map(id -> {
                    var product = productRepository.findById(id).orElseThrow(() ->
                            new RuntimeException("Product not found"));
                    return productMapper.toCardDto(product);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductCardDto> getAllByFilters(ProductCategoryEnum category, Map<String, Object> params, Pageable pageable) {
        params.put("category", category.name());
        return strategyFactory.getStrategy(category).filter(params, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> getAllCountries() {
        return productCountryRepository.findAll().stream().map(ProductCountry::getName).toList();
    }
}
