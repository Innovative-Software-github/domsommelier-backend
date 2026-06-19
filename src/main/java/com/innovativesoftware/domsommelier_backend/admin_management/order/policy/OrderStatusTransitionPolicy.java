package com.innovativesoftware.domsommelier_backend.admin_management.order.policy;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;

@Component
public class OrderStatusTransitionPolicy {

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
            "NEW", Set.of("COMPLETED", "CANCELLED"),
            "COMPLETED", Set.of(),
            "CANCELLED", Set.of()
    );

    public void validateTransition(String currentStatus, String newStatus) {
        if (currentStatus.equals(newStatus)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Статус уже установлен: " + newStatus);
        }

        Set<String> allowed = ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of());
        if (!allowed.contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Недопустимый переход статуса: " + currentStatus + " → " + newStatus
            );
        }
    }
}
