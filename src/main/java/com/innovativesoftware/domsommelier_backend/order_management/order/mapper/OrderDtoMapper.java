package com.innovativesoftware.domsommelier_backend.order_management.order.mapper;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.OrderItem;
import com.innovativesoftware.domsommelier_backend.order_management.order.model.OrderedProductDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderDtoMapper {

    public BigDecimal resolveTotalAmount(Order order) {
        if (order.getTotalAmount() != null) {
            return order.getTotalAmount();
        }
        return calculateTotalFromCurrentPrices(order.getOrderItems());
    }

    /**
     * Сумма позиций по прайсу. У заказов, оформленных до появления скидок, снапшота нет —
     * тогда это просто сумма зафиксированных цен позиций (скидок в них и не было).
     */
    public BigDecimal resolveItemsTotal(Order order) {
        if (order.getItemsTotal() != null) {
            return order.getItemsTotal();
        }
        return sumOfItems(order.getOrderItems());
    }

    public List<OrderedProductDto> mapOrderItems(List<OrderItem> items) {
        if (items == null) {
            return List.of();
        }
        return items.stream()
                .map(item -> {
                    BigDecimal price = item.getUnitPrice() != null
                            ? item.getUnitPrice()
                            : item.getProduct().getPrice();
                    return OrderedProductDto.builder()
                            .productId(item.getProduct().getId())
                            .name(item.getProduct().getName())
                            .article(item.getProduct().getArticle())
                            .quantity(item.getQuantity())
                            .price(price)
                            .sum(price.multiply(BigDecimal.valueOf(item.getQuantity())))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public String resolvePickupAddress(Order order) {
        WineStore wineStore = order.getWineStore();
        if (wineStore == null) {
            return "Неизвестно";
        }
        return wineStore.getName() + ", " + wineStore.getAddress();
    }

    public String resolveCustomerName(Order order) {
        if (order.getCustomerName() != null && !order.getCustomerName().isBlank()) {
            return order.getCustomerName();
        }
        Customer customer = order.getCustomer();
        if (customer == null) {
            return null;
        }
        String firstName = customer.getFirstName() != null ? customer.getFirstName() : "";
        String secondName = customer.getSecondName() != null ? customer.getSecondName() : "";
        String fullName = (firstName + " " + secondName).trim();
        return fullName.isEmpty() ? customer.getEmail() : fullName;
    }

    public String resolveCustomerPhone(Order order) {
        if (order.getCustomerPhone() != null && !order.getCustomerPhone().isBlank()) {
            return order.getCustomerPhone();
        }
        return order.getCustomer() != null ? order.getCustomer().getPhone() : null;
    }

    private BigDecimal calculateTotalFromCurrentPrices(List<OrderItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Сумма по зафиксированным в заказе ценам позиций. */
    private BigDecimal sumOfItems(List<OrderItem> items) {
        if (items == null) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(item -> {
                    BigDecimal price = item.getUnitPrice() != null
                            ? item.getUnitPrice()
                            : item.getProduct().getPrice();
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
