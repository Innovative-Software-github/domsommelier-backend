package com.innovativesoftware.domsommelier_backend.admin_management.order.spec;

import com.innovativesoftware.domsommelier_backend.admin_management.order.model.AdminOrderFilterRequest;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class OrderSpecifications {

    private OrderSpecifications() {
    }

    public static Specification<Order> fromFilter(AdminOrderFilterRequest filter) {
        Specification<Order> spec = Specification.where(null);

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            spec = spec.and(hasStatus(filter.getStatus()));
        }
        if (filter.getWineStoreId() != null) {
            spec = spec.and(hasWineStoreId(filter.getWineStoreId()));
        }
        if (filter.getDateFrom() != null) {
            spec = spec.and(createdAtFrom(filter.getDateFrom()));
        }
        if (filter.getDateTo() != null) {
            spec = spec.and(createdAtTo(filter.getDateTo()));
        }
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            spec = spec.and(matchesQuery(filter.getQuery().trim()));
        }

        return spec;
    }

    private static Specification<Order> hasStatus(String status) {
        return (root, query, cb) ->
                cb.equal(root.join("orderStatus", JoinType.INNER).get("name"), status);
    }

    private static Specification<Order> hasWineStoreId(Long wineStoreId) {
        return (root, query, cb) ->
                cb.equal(root.join("wineStore", JoinType.INNER).get("id"), wineStoreId);
    }

    private static Specification<Order> createdAtFrom(java.time.LocalDate dateFrom) {
        OffsetDateTime from = dateFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    private static Specification<Order> createdAtTo(java.time.LocalDate dateTo) {
        OffsetDateTime to = dateTo.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    private static Specification<Order> matchesQuery(String query) {
        return (root, criteriaQuery, cb) -> {
            try {
                UUID orderId = UUID.fromString(query);
                return cb.equal(root.get("id"), orderId);
            } catch (IllegalArgumentException ignored) {
                String pattern = "%" + query.toLowerCase() + "%";
                var customerJoin = root.join("customer", JoinType.LEFT);
                return cb.or(
                        cb.like(cb.lower(root.get("customerName")), pattern),
                        cb.like(cb.lower(root.get("customerPhone")), pattern),
                        cb.like(cb.lower(customerJoin.get("email")), pattern)
                );
            }
        };
    }
}
