package com.innovativesoftware.domsommelier_backend.customer_management.customer.repository;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.model.CustomerRecommendationsProjection;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
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
}