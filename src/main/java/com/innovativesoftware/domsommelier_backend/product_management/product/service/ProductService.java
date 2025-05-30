package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.google.gson.Gson;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final Gson gson;
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private ProductCountryRepository productCountryRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private final ProductFilterStrategyFactory strategyFactory;

    public List<ProductCountryProjection> getCountriesWithWines() {
        return productCountryRepository.getCountriesWithProductCategory(ProductCategoryEnum.WINE);
    }

    public List<ProductCategoryProjection> getProductCategories() {
        return productCategoryRepository.findAllCategories();
    }

    public String searchProductsByName(String name, Pageable pageable) {
        Page<UUID> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        return gson.toJson(products.getContent()); // или вернуть сам Page если нужно totalElements/totalPages
    }

    public ProductDTO getProductDetails(UUID productId) {
        var product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductMapper.toProductDto(product);
    }

    public List<ProductCardDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .stream()
                .map(ProductMapper::toCardDto)
                .toList();
    }

    public List<ProductCardDto> getAllProductsByCategory(ProductCategoryEnum productCategory, Pageable pageable) {
        return productRepository.findByProductCategory(productCategory, pageable)
                .stream()
                .map(id -> {
                    var product = productRepository.findById(id).orElseThrow(() ->
                            new RuntimeException("Product not found"));
                    return ProductMapper.toCardDto(product);
                })
                .toList();
    }

    public List<ProductCardDto> getAllProductsByCountry(String country, Pageable pageable) {
        return productRepository.findByProductCountry(country.toLowerCase(), pageable)
                .stream()
                .map(id -> {
                    var product = productRepository.findById(id).orElseThrow(() ->
                            new RuntimeException("Product not found"));
                    return ProductMapper.toCardDto(product);
                })
                .toList();
    }

    public List<ProductCardDto> getAllByFilters(ProductCategoryEnum category, Map<String, Object> params, Pageable pageable) {
        params.put("category", category.name());
        return strategyFactory.getStrategy(category).filter(params, pageable);
    }
}
