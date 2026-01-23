package com.innovativesoftware.domsommelier_backend.order_management.order.service;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Address;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer.repository.AddressRepository;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.discount.repository.PromoRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderItem;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderStatus;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderFullDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderHistoryDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderedProductDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderItemRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderStatusRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final PromoRepository promoRepository;

    @Transactional
    public Order createOrderFromBasket(BasketDto basket, UUID customerId, UUID addressId) {
        // 1. Получаем покупателя и адрес
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Покупатель не найден"));

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoSuchElementException("Адрес не найден"));

        // 2. Определяем статус заказа
        OrderStatus status = orderStatusRepository.findById("NEW")
                .orElseGet(() -> {
                    OrderStatus s = new OrderStatus();
                    s.setName("NEW");
                    return orderStatusRepository.save(s);
                });

        // 3. Инициализируем заказ
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCreatedAt(OffsetDateTime.now());
        order.setCustomer(customer);
        order.setAddress(address);
        order.setOrderStatus(status);

        // 4. Промокод (если есть)
        /*if (basket.getPromoId() != null) {
            Promo promo = promoRepository.findById(basket.getPromoId())
                    .orElseThrow(() -> new NoSuchElementException("Промокод не найден"));

            PromoUse promoUse = new PromoUse();
            promoUse.setPromo(promo);
            promoUse.setCustomer(customer);
            promoUse.setOrder(order);
            promoUse.setIsUsed(true);
            order.setPromoUse(promoUse);
        }*/

        // 5. OrderItems
        List<OrderItem> orderItems = basket.getItems().stream()
                .map(basketItemDto -> {
                    Product product = productRepository.findById(basketItemDto.getProduct().getId())
                            .orElseThrow(() -> new NoSuchElementException("Товар не найден: " + basketItemDto.getProduct().getId()));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(basketItemDto.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // 6. Сохраняем заказ (всё сохраняется каскадно)
        Order savedOrder = orderRepository.save(order);

        // 7. Сохраняем позиции заказа отдельно, если не настроен cascade (optional)
        orderItems.forEach(orderItemRepository::save);

        return savedOrder;
    }

    public Optional<Order> findOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    public List<Order> findOrdersByCustomer(UUID customerId) {
        return orderRepository.findAllByCustomerId(customerId);
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Заказ не найден"));
        // только если статус позволяет отмену
        if (!"NEW".equals(order.getOrderStatus().getName())) {
            throw new IllegalStateException("Заказ нельзя отменить");
        }
        OrderStatus cancelledStatus = orderStatusRepository.findById("CANCELLED")
                .orElseGet(() -> {
                    OrderStatus s = new OrderStatus();
                    s.setName("CANCELLED");
                    return orderStatusRepository.save(s);
                });
        order.setOrderStatus(cancelledStatus);
        orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryDto> getCustomerOrdersHistory(UUID customerId, Pageable pageable) {
        return orderRepository.findAllByCustomerId(customerId, pageable)
                .map(this::mapToHistoryDto);
    }

    @Transactional(readOnly = true)
    public OrderFullDto getOrderDetails(UUID orderId, UUID customerId) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ не найден"));

        if (!order.getCustomer().getId().equals(customerId)) {
            throw new AccessDeniedException("Вы не можете просматривать чужой заказ");
        }

        return mapToFullDto(order);
    }

    private OrderHistoryDto mapToHistoryDto(Order order) {
        List<OrderItem> items = order.getOrderItems();
        BigDecimal total = calculateTotal(items);

        String preview = items.isEmpty() ? "Нет товаров" : items.get(0).getProduct().getName();
        if (items.size() > 1) {
            preview += " и еще " + (items.size() - 1) + " поз.";
        }

        return OrderHistoryDto.builder()
                .id(order.getId())
                .date(order.getCreatedAt())
                .statusName(order.getOrderStatus().getName())
                .totalAmount(total)
                .previewText(preview)
                .build();
    }

    private OrderFullDto mapToFullDto(Order order) {
        List<OrderItem> items = order.getOrderItems();

        List<OrderedProductDto> productDtos = items.stream().map(item -> {
            BigDecimal currentPrice = item.getProduct().getPrice(); // ВАЖНО: берем текущую цену, т.к. исторической нет в OrderItem
            return OrderedProductDto.builder()
                    .productId(item.getProduct().getId())
                    .name(item.getProduct().getName())
                    .article(item.getProduct().getArticle())
                    .quantity(item.getQuantity())
                    .price(currentPrice)
                    .sum(currentPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

        String addressString = "Винотека на ул. " +
                (order.getAddress() != null ? order.getAddress().getId() : "Неизвестно");

        return OrderFullDto.builder()
                .id(order.getId())
                .date(order.getCreatedAt())
                .statusName(order.getOrderStatus().getName())
                .pickupAddress(addressString)
                .totalAmount(calculateTotal(items))
                .items(productDtos)
                .build();
    }

    private BigDecimal calculateTotal(List<OrderItem> items) {
        if (items == null) return BigDecimal.ZERO;
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
