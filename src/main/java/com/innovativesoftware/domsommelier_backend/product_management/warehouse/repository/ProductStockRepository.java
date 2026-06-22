package com.innovativesoftware.domsommelier_backend.product_management.warehouse.repository;

import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductStockRepository extends JpaRepository<ProductStock, UUID> {

    List<ProductStock> findAllByProduct_Id(UUID productId);

    Optional<ProductStock> findByProduct_IdAndWineStore_Id(UUID productId, Long wineStoreId);

    /** Остатки указанных товаров во всех винотеках (для расчёта доступности корзины по точкам). */
    List<ProductStock> findByProduct_IdIn(Collection<UUID> productIds);

    /** Остатки указанных товаров в конкретной винотеке (для построения склада магазина). */
    List<ProductStock> findByWineStore_IdAndProduct_IdIn(Long wineStoreId, Collection<UUID> productIds);

    /** Есть ли товар в наличии (quantity > 0) хотя бы в одной винотеке города. */
    boolean existsByProduct_IdAndWineStore_CityAndQuantityGreaterThan(UUID productId, String city, int quantity);

    void deleteByProduct_Id(UUID productId);
}
