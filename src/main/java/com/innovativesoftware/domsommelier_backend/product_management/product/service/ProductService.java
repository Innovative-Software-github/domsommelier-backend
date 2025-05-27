package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.google.gson.Gson;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCategoryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final EntityManager entityManager;
    @Autowired
    private final CreateQuery createQuery;

    public List<ProductCountryProjection> getCountriesWithWines() {
        return productCountryRepository.getCountriesWithProductCategory(ProductCategoryEnum.WINE);
    }

    public List<ProductCategoryProjection> getProductCategories() {
        return productCategoryRepository.findAllCategories();
    }

    public String searchProductsByName(String name) {
        List<UUID> products = productRepository.findByNameContainingIgnoreCase(name);
        return gson.toJson(products);
    }

    public ProductDTO getProductDetails(UUID productId) {
        var product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        return ProductMapper.toProductDto(product);
    }

    public List<ProductCardDto> getAllProducts() {
        return productRepository.findAll().stream().map(ProductMapper::toCardDto).toList();
    }

    public List<ProductCardDto> getAllProductsByCategory(String productCategory) {
        return productRepository.findByProductCategory(ProductCategoryEnum.valueOf(productCategory)).stream().map(
                id -> {
                    var product = productRepository.findById(id).orElseThrow(() ->
                            new RuntimeException("Product not found"));
                    return ProductMapper.toCardDto(product);
                }
        ).toList();
    }

    public List<ProductCardDto> getAllProductsByCountry(String country) {
        return productRepository.findByProductCountry(country.toLowerCase()).stream().map(
                id -> {
                    var product = productRepository.findById(id).orElseThrow(() ->
                            new RuntimeException("Product not found"));
                    return ProductMapper.toCardDto(product);
                }
        ).toList();
    }

    public List<ProductCardDto> getAllByFilters(ProductCategoryEnum category, Map<String, Object> params) {
        return entityManager.createQuery(createQuery.createQuery(category, params)).getResultList().stream().map(
                id -> {
                    var product = productRepository.findById(id).orElseThrow(() ->
                            new RuntimeException("Product not found"));
                    return ProductMapper.toCardDto(product);
                }
        ).toList();
    }

    private Object getProductType(UUID id) {
        var product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        switch (product.getProductCategory().getName()) {
            case WINE -> {
                return Wine.class;
            }
            /*
            case SPIRIT -> {
                return Spirit.class;
            }
            case CHAMPAGNE_AND_SPARKLING -> {
                return ChampagneAndSparkling.class;
            }
            case LOW_ALCOHOL -> {
                return LowAlcohol.class;
            }
            case SNACK -> {
                return Snack.class;
            }
            case ACCESSORIES -> {
                return Accessories.class;
            }*/
            default -> {
                return null;
            }
        }
    }
}
