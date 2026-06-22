package com.innovativesoftware.domsommelier_backend.order_management.order.service;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.CheckoutRequestDto;
import com.innovativesoftware.domsommelier_backend.order_management.discount.repository.PromoRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderItem;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderStatus;
import com.innovativesoftware.domsommelier_backend.order_management.order.mapper.OrderDtoMapper;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderFullDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderHistoryDto;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderItemRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderStatusRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.repository.WineStoreRepository;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.ProductStock;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.repository.ProductStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
    private final WineStoreRepository wineStoreRepository;
    private final ProductStockRepository productStockRepository;
    private final PromoRepository promoRepository;
    private final OrderDtoMapper orderDtoMapper;

    @Transactional
    public Order createOrderFromBasket(BasketDto basket, UUID customerId, Long wineStoreId,
                                       CheckoutRequestDto checkoutData) {
        if (basket.getItems() == null || basket.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Корзина пуста");
        }

        // 1. Получаем покупателя и винотеку
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Покупатель не найден"));

        WineStore wineStore = wineStoreRepository.findById(wineStoreId)
                .orElseThrow(() -> new EntityNotFoundException("Винотека не найдена: " + wineStoreId));

        // 1.5 Проверяем, что все товары есть в наличии в выбранной винотеке (наличие + количество)
        validateStockAvailability(basket, wineStore);

        // 2. Определяем статус заказа
        OrderStatus status = orderStatusRepository.findById("NEW")
                .orElseGet(() -> {
                    OrderStatus s = new OrderStatus();
                    s.setName("NEW");
                    return orderStatusRepository.save(s);
                });

        // 3. Инициализируем заказ
        Order order = new Order();
        order.setCreatedAt(OffsetDateTime.now());
        order.setCustomer(customer);
        order.setWineStore(wineStore);
        order.setOrderStatus(status);

        if (checkoutData != null) {
            order.setCustomerName(checkoutData.getCustomerName());
            order.setCustomerPhone(checkoutData.getCustomerPhone());
            order.setPickupDate(checkoutData.getPickupDate());
            order.setPaymentMethod(
                    checkoutData.getPaymentMethod() != null
                            ? checkoutData.getPaymentMethod().toLowerCase()
                            : null
            );
        }

        // 4. OrderItems со snapshot цены
        List<OrderItem> orderItems = basket.getItems().stream()
                .map(basketItemDto -> {
                    Product product = productRepository.findById(basketItemDto.getProduct().getId())
                            .orElseThrow(() -> new NoSuchElementException("Товар не найден: " + basketItemDto.getProduct().getId()));
                    BigDecimal unitPrice = product.getPrice();
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(basketItemDto.getQuantity());
                    orderItem.setUnitPrice(unitPrice);
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // 5. Считаем totalAmount из snapshot (с учётом скидки корзины)
        BigDecimal rawTotal = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int discount = basket.getDiscount() != null ? basket.getDiscount() : 0;
        BigDecimal total = discount > 0
                ? rawTotal.subtract(rawTotal.multiply(BigDecimal.valueOf(discount))
                        .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP))
                : rawTotal;
        order.setTotalAmount(total);

        // 6. Сохраняем заказ вместе с позициями (cascade)
        return orderRepository.save(order);
    }

    /**
     * Проверяет, что каждая позиция корзины доступна в выбранной винотеке в нужном количестве.
     * Источник правды — остаток на пару (товар, винотека). Если у товара нет строки остатка
     * в этой точке (например, он из ассортимента другого города), доступное количество = 0.
     * Все проблемные позиции собираются в одно сообщение и отдаются как 409 Conflict.
     */
    private void validateStockAvailability(BasketDto basket, WineStore wineStore) {
        List<String> problems = basket.getItems().stream()
                .map(item -> {
                    int requested = item.getQuantity();
                    int available = productStockRepository
                            .findByProduct_IdAndWineStore_Id(item.getProduct().getId(), wineStore.getId())
                            .map(ProductStock::getQuantity)
                            .orElse(0);
                    if (available >= requested) {
                        return null;
                    }
                    String name = item.getProduct().getName();
                    return available == 0
                            ? String.format("«%s» — нет в наличии", name)
                            : String.format("«%s» — доступно %d из %d", name, available, requested);
                })
                .filter(Objects::nonNull)
                .toList();

        if (!problems.isEmpty()) {
            throw new IllegalStateException(
                    "Некоторые товары недоступны в винотеке «" + wineStore.getName()
                            + "» (" + wineStore.getCity() + "): " + String.join("; ", problems)
                            + ". Измените винотеку или состав заказа.");
        }
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
        BigDecimal total = orderDtoMapper.resolveTotalAmount(order);

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
        return OrderFullDto.builder()
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
                .build();
    }
}
