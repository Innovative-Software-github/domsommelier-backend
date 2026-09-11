package com.innovativesoftware.domsommelier_backend.admin_management.order.service;

import com.innovativesoftware.domsommelier_backend.admin_management.order.model.AdminOrderDetailDto;
import com.innovativesoftware.domsommelier_backend.admin_management.order.model.AdminOrderFilterRequest;
import com.innovativesoftware.domsommelier_backend.admin_management.order.model.AdminOrderListDto;
import com.innovativesoftware.domsommelier_backend.admin_management.order.model.OrderStatusOptionDto;
import com.innovativesoftware.domsommelier_backend.admin_management.order.policy.OrderStatusTransitionPolicy;
import com.innovativesoftware.domsommelier_backend.admin_management.order.spec.OrderSpecifications;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderStatus;
import com.innovativesoftware.domsommelier_backend.order_management.order.mapper.OrderDtoMapper;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private static final Map<String, String> STATUS_LABELS = Map.of(
            "NEW", "Новый",
            "COMPLETED", "Выполнен",
            "CANCELLED", "Отменён"
    );

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderDtoMapper orderDtoMapper;
    private final OrderStatusTransitionPolicy transitionPolicy;

    @Transactional(readOnly = true)
    public Page<AdminOrderListDto> getOrders(AdminOrderFilterRequest filter, Pageable pageable) {
        Specification<Order> spec = OrderSpecifications.fromFilter(filter);
        return orderRepository.findAll(spec, pageable).map(this::mapToListDto);
    }

    @Transactional(readOnly = true)
    public AdminOrderDetailDto getOrder(UUID orderId) {
        Order order = findOrderWithDetails(orderId);
        return mapToDetailDto(order);
    }

    @Transactional
    public AdminOrderDetailDto updateStatus(UUID orderId, String newStatus) {
        Order order = findOrderWithDetails(orderId);
        String currentStatus = order.getOrderStatus().getName();

        transitionPolicy.validateTransition(currentStatus, newStatus);

        OrderStatus status = orderStatusRepository.findById(newStatus)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Неизвестный статус: " + newStatus
                ));

        order.setOrderStatus(status);
        orderRepository.save(order);
        return mapToDetailDto(order);
    }

    @Transactional
    public AdminOrderDetailDto cancelOrder(UUID orderId) {
        return updateStatus(orderId, "CANCELLED");
    }

    @Transactional(readOnly = true)
    public List<OrderStatusOptionDto> getOrderStatuses() {
        return orderStatusRepository.findAll().stream()
                .sorted(Comparator.comparing(OrderStatus::getName))
                .map(status -> OrderStatusOptionDto.builder()
                        .name(status.getName())
                        .label(STATUS_LABELS.getOrDefault(status.getName(), status.getName()))
                        .build())
                .toList();
    }

    private Order findOrderWithDetails(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ не найден"));
    }

    private AdminOrderListDto mapToListDto(Order order) {
        return AdminOrderListDto.builder()
                .id(order.getId())
                .createdAt(order.getCreatedAt())
                .customerName(orderDtoMapper.resolveCustomerName(order))
                .customerPhone(orderDtoMapper.resolveCustomerPhone(order))
                .wineStoreId(order.getWineStore() != null ? order.getWineStore().getId() : null)
                .wineStoreName(order.getWineStore() != null ? order.getWineStore().getName() : null)
                .totalAmount(orderDtoMapper.resolveTotalAmount(order))
                .statusName(order.getOrderStatus().getName())
                .build();
    }

    private AdminOrderDetailDto mapToDetailDto(Order order) {
        Integer promoDiscount = order.getPromoUse() != null && order.getPromoUse().getPromo() != null
                ? order.getPromoUse().getPromo().getDiscount()
                : null;

        return AdminOrderDetailDto.builder()
                .id(order.getId())
                .date(order.getCreatedAt())
                .statusName(order.getOrderStatus().getName())
                .pickupAddress(orderDtoMapper.resolvePickupAddress(order))
                .totalAmount(orderDtoMapper.resolveTotalAmount(order))
                .items(orderDtoMapper.mapOrderItems(order.getOrderItems()))
                .customerPhone(orderDtoMapper.resolveCustomerPhone(order))
                .customerName(orderDtoMapper.resolveCustomerName(order))
                .pickupDate(order.getPickupDate())
                .paymentMethod(order.getPaymentMethod())
                .customerId(order.getCustomer() != null ? order.getCustomer().getId() : null)
                .customerEmail(order.getCustomer() != null ? order.getCustomer().getEmail() : null)
                .wineStoreId(order.getWineStore() != null ? order.getWineStore().getId() : null)
                .promoDiscount(promoDiscount)
                .itemsTotal(orderDtoMapper.resolveItemsTotal(order))
                .saleDiscountAmount(order.getSaleDiscountAmount())
                .personalDiscountPercent(order.getPersonalDiscountPercent())
                .personalDiscountAmount(order.getPersonalDiscountAmount())
                .build();
    }
}
