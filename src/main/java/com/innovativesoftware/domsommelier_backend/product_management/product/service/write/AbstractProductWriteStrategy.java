package com.innovativesoftware.domsommelier_backend.product_management.product.service.write;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.ProductCountry;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Общая часть стратегий записи: создание базового {@code Product},
 * применение скаляров и резолв справочников (страна, категория).
 */
public abstract class AbstractProductWriteStrategy implements ProductWriteStrategy {

    protected final ProductCountryRepository productCountryRepository;
    protected final ProductCategoryRepository productCategoryRepository;

    protected AbstractProductWriteStrategy(ProductCountryRepository productCountryRepository,
                                           ProductCategoryRepository productCategoryRepository) {
        this.productCountryRepository = productCountryRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    protected Product newBaseProduct(ProductWriteRequest request) {
        Product product = new Product();
        product.setCreatedAt(OffsetDateTime.now());
        applyBaseFields(product, request);
        return product;
    }

    protected void applyBaseFields(Product product, ProductWriteRequest request) {
        if (request.isBrandProvided()) product.setBrand(request.getBrand());
        if (request.isPackagingProvided()) product.setPackaging(request.getPackaging());
        product.setArticle(request.getArticle().trim());
        product.setName(request.getName().trim());
        product.setInitialPrice(request.getInitialPrice());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setAroma(request.getAroma());
        product.setTaste(request.getTaste());
        product.setFoodPairing(request.getFoodPairing());
        product.setSalePrice(normalizeSalePrice(request.getSalePrice()));
        product.setProductCountry(resolveCountry(request.getCountry()));
        product.setProductCategory(resolveCategory(request.getCategory()));
    }

    private ProductCountry resolveCountry(String name) {
        return productCountryRepository.findById(name.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Страна не найдена: " + name));
    }

    private ProductCategory resolveCategory(ProductCategoryEnum category) {
        return productCategoryRepository.findByName(category)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Категория не найдена: " + category));
    }

    protected <T extends ProductWriteRequest> T cast(ProductWriteRequest request, Class<T> type) {
        if (!type.isInstance(request)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный тип запроса для категории");
        }
        return type.cast(request);
    }

    /**
     * Пустая акция (не заполнено или 0) хранится как {@code null} — иначе {@code salePrice = 0}
     * читалось бы как «товар бесплатный» и обнуляло бы эффективную цену.
     */
    private BigDecimal normalizeSalePrice(BigDecimal salePrice) {
        return salePrice == null || salePrice.signum() <= 0 ? null : salePrice;
    }

    protected String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Заменяет значения element-collection, переиспользуя существующий список Hibernate
     * (на update нельзя подменять ссылку на управляемую коллекцию).
     */
    protected List<String> replaceStrings(List<String> existing, List<String> values) {
        List<String> sanitized = values == null ? List.of()
                : values.stream().filter(v -> v != null && !v.isBlank()).map(String::trim).toList();
        if (existing == null) {
            return new ArrayList<>(sanitized);
        }
        existing.clear();
        existing.addAll(sanitized);
        return existing;
    }
}
