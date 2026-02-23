package com.innovativesoftware.domsommelier_backend.order_management.order.controller;

import com.innovativesoftware.domsommelier_backend.auth_management.AppUserDetails;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderFullDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderHistoryDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Управление заказами пользователя")
public class OrderController {

    private final OrderService orderService;

    // 1. ПРОСМОТР ИСТОРИИ
    @Operation(summary = "Получить историю заказов")
    @GetMapping
    public ResponseEntity<Page<OrderHistoryDto>> getMyOrders(
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        UUID customerId = getUserId(userDetails);
        Page<OrderHistoryDto> orders = orderService.getCustomerOrdersHistory(customerId, pageable);
        return ResponseEntity.ok(orders);
    }

    // 2. ПРОСМОТР ДЕТАЛЕЙ
    @Operation(summary = "Получить детали заказа")
    @GetMapping("/{id}")
    public ResponseEntity<OrderFullDto> getOrderDetails(
            @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails
    ) throws AccessDeniedException {
        UUID customerId = getUserId(userDetails);
        // Сервис сам проверит, совпадает ли customerId заказа с текущим
        OrderFullDto orderDetails = orderService.getOrderDetails(id, customerId);
        return ResponseEntity.ok(orderDetails);
    }

    // 3. СОЗДАНИЕ ЗАКАЗА
    @Operation(summary = "Создать заказ")
    @PostMapping
    public ResponseEntity<UUID> createOrder(
            @RequestParam UUID addressId,
            @RequestBody BasketDto basketDto,
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails
    ) {
        UUID customerId = getUserId(userDetails);
        Order createdOrder = orderService.createOrderFromBasket(basketDto, customerId, addressId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder.getId());
    }

    // 4. ОТМЕНА ЗАКАЗА
    @Operation(summary = "Отменить заказ")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal AppUserDetails userDetails
    ) throws AccessDeniedException {
        UUID customerId = getUserId(userDetails);

        // В хорошей архитектуре нужно передать customerId в метод cancelOrder для проверки прав,
        // но оставим пока как в твоей реализации, добавив проверку чтения
        orderService.getOrderDetails(id, customerId); // Проверка прав (упадет, если заказ чужой)
        orderService.cancelOrder(id);

        return ResponseEntity.ok().build();
    }

    // --- Private Helper ---

    private UUID getUserId(AppUserDetails userDetails) {
        if (userDetails == null) {
            // Если Security Context пуст, кидаем 401 Unauthorized
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не авторизован");
        }
        return userDetails.getId();
    }
}