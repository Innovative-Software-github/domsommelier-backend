package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.ProductCountry;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderItemRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWineCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWineColor;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SugarContent;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcoholCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.SnackCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.SpiritCategory;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.WineColor;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.WineType;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCardDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductReferenceDto;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.ProductWriteRequest;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.LowAlcoholCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductCountryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SnackCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineColorRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SpiritCategoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SugarContentRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineColorRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineTypeRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.service.write.ProductWriteStrategyRegistry;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.repository.StorageHistoryRepository;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminProductService {

    private final com.innovativesoftware.domsommelier_backend.product_management.product.attributes.AttributeReferenceService attributeReferences;
    private final ProductWriteStrategyRegistry strategyRegistry;
    private final ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductStockRepository productStockRepository;
    private final StorageHistoryRepository storageHistoryRepository;
    private final ProductPhotoOperationService productPhotoOperationService;
    private final ProductCountryRepository productCountryRepository;
    private final WineColorRepository wineColorRepository;
    private final WineTypeRepository wineTypeRepository;
    private final SpiritCategoryRepository spiritCategoryRepository;
    private final SparklingWineCategoryRepository sparklingWineCategoryRepository;
    private final SparklingWineColorRepository sparklingWineColorRepository;
    private final SugarContentRepository sugarContentRepository;
    private final LowAlcoholCategoryRepository lowAlcoholCategoryRepository;
    private final SnackCategoryRepository snackCategoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductCardDto> list(ProductCategoryEnum category, String search, Pageable pageable) {
        String normalizedSearch = (search == null) ? "" : search.trim();
        return productRepository.searchProductsByCategory(category, normalizedSearch, pageable)
                .map(productMapper::toCardDto);
    }

    @Transactional
    public UUID create(ProductWriteRequest request) {
        attributeReferences.validate(request);
        return strategyRegistry.get(request.getCategory()).create(request).getId();
    }

    @Transactional
    public void update(UUID id, ProductWriteRequest request) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден: " + id));

        if (existing.getProductCategory().getName() != request.getCategory()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Нельзя менять категорию товара");
        }

        attributeReferences.validate(request);
        strategyRegistry.get(request.getCategory()).update(id, request);
    }

    /**
     * Жёсткое удаление с защитой: нельзя удалить заказанный товар.
     * Перед удалением чистим все FK-связи, кроме заказов (избранное, склад, остатки, фото).
     */
    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден: " + id));

        if (orderItemRepository.existsByProduct_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Нельзя удалить товар: на него есть заказы");
        }

        productRepository.deleteFavoritesByProductId(id);
        storageHistoryRepository.deleteByProduct_Id(id);
        productStockRepository.deleteByProduct_Id(id);
        productPhotoOperationService.deletePhotosByProductId(id);

        strategyRegistry.get(product.getProductCategory().getName()).delete(id);
    }

    @Transactional(readOnly = true)
    public ProductReferenceDto getReference(ProductCategoryEnum category) {
        ProductReferenceDto.ProductReferenceDtoBuilder builder = ProductReferenceDto.builder()
                .countries(sorted(productCountryRepository.findAll().stream().map(ProductCountry::getName).toList()));

        switch (category) {
            case wine -> builder
                    .colors(sorted(wineColorRepository.findAll().stream().map(WineColor::getName).toList()))
                    .types(sorted(wineTypeRepository.findAll().stream().map(WineType::getName).toList()));
            case spirit -> builder
                    .subcategories(sorted(spiritCategoryRepository.findAll().stream().map(SpiritCategory::getName).toList()));
            case champagne_and_sparkling -> builder
                    .subcategories(sorted(sparklingWineCategoryRepository.findAll().stream().map(SparklingWineCategory::getName).toList()))
                    .colors(sorted(sparklingWineColorRepository.findAll().stream().map(SparklingWineColor::getName).toList()))
                    .sugarContents(sorted(sugarContentRepository.findAll().stream().map(SugarContent::getName).toList()));
            case low_alcohol -> builder
                    .subcategories(sorted(lowAlcoholCategoryRepository.findAll().stream().map(LowAlcoholCategory::getName).toList()));
            case snack -> builder
                    .subcategories(sorted(snackCategoryRepository.findAll().stream().map(SnackCategory::getName).toList()));
            case accessories -> {
                // только страны
            }
        }
        return builder.build();
    }

    private List<String> sorted(List<String> values) {
        return values.stream().filter(v -> v != null).sorted(Comparator.naturalOrder()).toList();
    }
}
