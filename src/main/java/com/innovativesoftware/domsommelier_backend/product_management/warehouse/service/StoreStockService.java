package com.innovativesoftware.domsommelier_backend.product_management.warehouse.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.repository.WineStoreRepository;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.ProductStock;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.model.StoreStockItemDto;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreStockService {

    private final WineStoreRepository wineStoreRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    /** Страница ассортимента с остатком каждого товара в указанной винотеке (0, если строки нет). */
    @Transactional(readOnly = true)
    public Page<StoreStockItemDto> getStoreStock(Long storeId, String search, ProductCategoryEnum category, Pageable pageable) {
        requireStoreExists(storeId);

        // Пустая строка = «без фильтра» (LIKE '%%'). Никогда не передаём null — иначе ломается биндинг в SQL.
        String normalizedSearch = (search == null) ? "" : search.trim();
        Page<Product> products = productRepository.searchForStock(normalizedSearch, category, pageable);

        List<UUID> productIds = products.getContent().stream().map(Product::getId).toList();
        Map<UUID, Integer> quantityByProduct = productIds.isEmpty()
                ? Map.of()
                : productStockRepository.findByWineStore_IdAndProduct_IdIn(storeId, productIds).stream()
                .collect(Collectors.toMap(stock -> stock.getProduct().getId(), ProductStock::getQuantity));

        return products.map(product -> toDto(product, quantityByProduct.getOrDefault(product.getId(), 0)));
    }

    /** Устанавливает остаток товара в винотеке (создаёт строку, если её ещё нет). */
    @Transactional
    public StoreStockItemDto setStock(Long storeId, UUID productId, int quantity) {
        if (quantity < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Количество не может быть отрицательным");
        }

        WineStore store = wineStoreRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Винотека не найдена: " + storeId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар не найден: " + productId));

        ProductStock stock = productStockRepository.findByProduct_IdAndWineStore_Id(productId, storeId)
                .orElseGet(() -> {
                    ProductStock created = new ProductStock();
                    created.setProduct(product);
                    created.setWineStore(store);
                    return created;
                });
        stock.setQuantity(quantity);
        productStockRepository.save(stock);

        return toDto(product, quantity);
    }

    private void requireStoreExists(Long storeId) {
        if (!wineStoreRepository.existsById(storeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Винотека не найдена: " + storeId);
        }
    }

    private StoreStockItemDto toDto(Product product, int quantity) {
        return StoreStockItemDto.builder()
                .productId(product.getId())
                .article(product.getArticle())
                .name(product.getName())
                .category(product.getProductCategory().getName())
                .price(product.getPrice())
                .quantity(quantity)
                .build();
    }
}
