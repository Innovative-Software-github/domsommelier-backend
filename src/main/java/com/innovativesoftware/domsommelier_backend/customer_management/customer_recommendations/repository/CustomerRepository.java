package com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.model.CustomerRecommendationsProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {
    @Query(value = """
            select grouped.*, product.name as productName, product.article as productArticle, product.price as productPrice
            from (select customer_id, product_id, count(order_id) as priority from order_item
            join orders ON orders.id = order_item.order_id
            where customer_id = ?1
            group by customer_id, product_id) as grouped
            join product on grouped.product_id = product.id
            order by priority DESC
            limit ?2""", nativeQuery = true)
    List<CustomerRecommendationsProjection> findCustomerRecommendations(UUID customerId, int limit);

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"defaultWineStore"})
    Optional<Customer> findWithDefaultWineStoreById(UUID id);
}