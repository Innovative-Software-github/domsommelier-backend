package com.innovativesoftware.domsommelier_backend.storagehistory.repository;

import com.innovativesoftware.domsommelier_backend.product.model.ProductNewProjection;
import com.innovativesoftware.domsommelier_backend.storagehistory.entity.StorageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface StorageHistoryRepository extends JpaRepository<StorageHistory, UUID> {
    @Query(value = """
            select products_new.product_id, amount_of_delivery, common_amount from
            (select product_id, count(amount) as amount_of_delivery from storage_history
            where amount > 0
            group by product_id
            having count(amount) = 1) as products_new
            inner join
            (select sum(amount) as common_amount, product_id from storage_history group by product_id having sum(amount) > 0) as products_in_stock ON
            products_new.product_id = products_in_stock.product_id
    """, nativeQuery = true)
    List<ProductNewProjection> findNewProductsInStorageHistory();
}