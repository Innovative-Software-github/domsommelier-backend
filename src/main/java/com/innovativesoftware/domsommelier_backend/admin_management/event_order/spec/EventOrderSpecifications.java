package com.innovativesoftware.domsommelier_backend.admin_management.event_order.spec;

import com.innovativesoftware.domsommelier_backend.admin_management.event_order.model.EventOrderFilterRequest;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.entity.EventOrder;
import com.innovativesoftware.domsommelier_backend.event_management.event_order.enums.EventOrderStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public final class EventOrderSpecifications {

    private EventOrderSpecifications() {
    }

    public static Specification<EventOrder> fromFilter(EventOrderFilterRequest filter) {
        Specification<EventOrder> spec = Specification.where(null);

        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            spec = spec.and(hasStatus(filter.getStatus()));
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

    private static Specification<EventOrder> hasStatus(String status) {
        EventOrderStatus parsed;
        try {
            parsed = EventOrderStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            // неизвестный статус — фильтр не применяем, вернётся пустой результат
            return (root, query, cb) -> cb.disjunction();
        }
        return (root, query, cb) -> cb.equal(root.get("status"), parsed);
    }

    private static Specification<EventOrder> createdAtFrom(LocalDate dateFrom) {
        OffsetDateTime from = dateFrom.atStartOfDay().atOffset(ZoneOffset.UTC);
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    private static Specification<EventOrder> createdAtTo(LocalDate dateTo) {
        OffsetDateTime to = dateTo.atTime(LocalTime.MAX).atOffset(ZoneOffset.UTC);
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    private static Specification<EventOrder> matchesQuery(String query) {
        return (root, criteriaQuery, cb) -> {
            try {
                UUID id = UUID.fromString(query);
                return cb.equal(root.get("id"), id);
            } catch (IllegalArgumentException ignored) {
                String pattern = "%" + query.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("phone")), pattern)
                );
            }
        };
    }
}
