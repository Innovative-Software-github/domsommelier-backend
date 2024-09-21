package com.innovativesoftware.domsommelier_backend.user;

import com.innovativesoftware.domsommelier_backend.entity.Customer;
import com.innovativesoftware.domsommelier_backend.user.model.ComplexDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query(value = "select * from (select customer_id, product_id, count(order_id) as orderedCount from order_item join orders ON orders.id = order_item.order_id\n" +
            "where customer_id = ?1\n" +
            "group by customer_id, product_id) as grouped\n" +
            "order by orderedCount DESC\n" +
            "limit ?2", nativeQuery = true)
    List<ComplexDTO> findPurchasesOfCustomer(UUID customerId, int limit);
}