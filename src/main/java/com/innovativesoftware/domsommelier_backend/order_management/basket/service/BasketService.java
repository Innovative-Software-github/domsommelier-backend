package com.innovativesoftware.domsommelier_backend.order_management.basket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.infrastructure.RedisService;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketItemDto;
import com.innovativesoftware.domsommelier_backend.order_management.discount.entity.Promo;
import com.innovativesoftware.domsommelier_backend.order_management.discount.repository.PromoRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.service.OrderService;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final RedisService redisService;
    private final ProductRepository productRepository;
    private final PromoRepository promoRepository;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    private String basketKey(UUID customerId) {
        return "basket:" + customerId;
    }

    // Получить корзину (если нет в Redis — создать пустую)
    public BasketDto getBasket(UUID customerId) {
        Object obj = redisService.getObject(basketKey(customerId));
        if (obj instanceof BasketDto basket) {
            return recalculateBasket(basket); // всегда пересчитываем сумму
        }
        return BasketDto.builder().customerId(customerId).build();
    }

    // Добавить товар в корзину
    public BasketDto addItem(UUID customerId, UUID productId, int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be positive");
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        BasketDto basket = getBasket(customerId);
        List<BasketItemDto> updatedItems = new ArrayList<>(basket.getItems());

        Optional<BasketItemDto> existing = updatedItems.stream()
                .filter(it -> it.getProduct().getId().equals(productId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(quantity);
        } else {
            // Новый товар
            updatedItems.add(BasketItemDto.builder()
                    .product(BasketItemDto.BasketItemDtoIdClass.builder()
                            .id(productId)
                            .name(product.getName())
                            .article(product.getArticle())
                            .price(product.getPrice())
                            .discount(product.getDiscount())
                            .productCountry(product.getProductCountry().getName())
                            .productCategoryName(product.getProductCategory().getName().name())
                            .productPhoto(product.getProductPhoto().stream().map(
                                    photo -> objectMapper.convertValue(
                                            photo, FileDTO.class
                                    )
                            ).toList())
                            .build())
                    .quantity(quantity)
                    .build());
        }
        basket.setItems(updatedItems);
        BasketDto result = recalculateBasket(basket);
        redisService.save(basketKey(customerId), result);
        return result;
    }

    public BasketDto removeItem(UUID customerId, UUID productId) {
        BasketDto basket = getBasket(customerId);
        List<BasketItemDto> updated = basket.getItems().stream()
                .filter(item -> !item.getProduct().getId().equals(productId))
                .toList();

        basket.setItems(updated);
        BasketDto result = recalculateBasket(basket);
        redisService.save(basketKey(customerId), result);
        return result;
    }

    public void clearBasket(UUID customerId) {
        redisService.remove(basketKey(customerId));
    }

    public BasketDto applyPromo(UUID customerId, UUID promoId) {
        Promo promo = promoRepository.findById(promoId)
                .orElseThrow(() -> new NoSuchElementException("Promo not found"));

        BasketDto basket = getBasket(customerId);
        basket.setDiscount(promo.getDiscount());
        BasketDto result = recalculateBasket(basket);
        redisService.save(basketKey(customerId), result);
        return result;
    }

    public BasketDto removePromo(UUID customerId) {
        BasketDto basket = getBasket(customerId);
        basket.setDiscount(0);
        BasketDto result = recalculateBasket(basket);
        redisService.save(basketKey(customerId), result);
        return result;
    }

    // Изменение количества
    public BasketDto updateQuantity(UUID customerId, UUID productId, int quantity) {
        if (quantity <= 0) {
            return removeItem(customerId, productId);
        }
        return addItem(customerId, productId, quantity);
    }

    private BasketDto recalculateBasket(BasketDto basket) {
        BigDecimal total = basket.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int discount = basket.getDiscount() != null ? basket.getDiscount() : 0;
        BigDecimal discounted = discount > 0
                ? total.subtract(total.multiply(
                        BigDecimal.valueOf(discount)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                : total;

        basket.setTotalPrice(total);
        basket.setDiscountedPrice(discounted);
        return basket;
    }

    // Оформить заказ (Basket -> Order)
    public UUID checkoutBasket(UUID customerId, UUID addressId) {
        BasketDto basket = getBasket(customerId);
        Order order = orderService.createOrderFromBasket(basket, customerId, addressId);
        clearBasket(customerId);
        return order.getId();
    }
}
