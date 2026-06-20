package com.innovativesoftware.domsommelier_backend.admin_management.customer.spec;

import com.innovativesoftware.domsommelier_backend.admin_management.customer.model.AdminCustomerFilterRequest;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public final class CustomerSpecifications {

    private CustomerSpecifications() {
    }

    public static Specification<Customer> fromFilter(AdminCustomerFilterRequest filter) {
        Specification<Customer> spec = Specification.where(fetchDefaultWineStore());

        if (filter.getRole() != null) {
            spec = spec.and(hasRole(filter.getRole()));
        }
        if (filter.getQuery() != null && !filter.getQuery().isBlank()) {
            spec = spec.and(matchesQuery(filter.getQuery().trim()));
        }

        return spec;
    }

    private static Specification<Customer> fetchDefaultWineStore() {
        return (root, query, cb) -> {
            if (query != null && Customer.class.equals(query.getResultType())) {
                // DISTINCT не используем: wine_store.location имеет тип point без оператора равенства в PostgreSQL
                root.fetch("defaultWineStore", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }

    private static Specification<Customer> hasRole(Role role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    private static Specification<Customer> matchesQuery(String queryText) {
        return (root, query, cb) -> {
            try {
                UUID customerId = UUID.fromString(queryText);
                return cb.equal(root.get("id"), customerId);
            } catch (IllegalArgumentException ignored) {
                String pattern = "%" + queryText.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("phone")), pattern),
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("secondName")), pattern),
                        cb.like(cb.lower(root.get("middleName")), pattern)
                );
            }
        };
    }
}
