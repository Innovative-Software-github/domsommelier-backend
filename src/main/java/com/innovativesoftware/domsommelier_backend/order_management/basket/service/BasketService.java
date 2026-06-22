package com.innovativesoftware.domsommelier_backend.order_management.basket.service;

import com.innovativesoftware.domsommelier_backend.infrastructure.RedisService;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketItemDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.CheckoutRequestDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.StoreAvailabilityDto;
import com.innovativesoftware.domsommelier_backend.order_management.discount.entity.Promo;
import com.innovativesoftware.domsommelier_backend.order_management.discount.repository.PromoRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.entity.Order;
import com.innovativesoftware.domsommelier_backend.order_management.order.service.OrderService;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductPhotoUrls;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.repository.WineStoreRepository;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.ProductStock;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasketService {

    private final RedisService redisService;
    private final ProductRepository productRepository;
    private final PromoRepository promoRepository;
    private final OrderService orderService;
    private final WineStoreRepository wineStoreRepository;
    private final ProductStockRepository productStockRepository;

    private String basketKey(UUID customerId) {
        return "basket:" + customerId;
    }

    /**
     * Доступность текущей корзины по всем винотекам: для каждой точки — хватает ли остатков
     * на все позиции корзины (наличие + количество). Используется модалкой выбора винотеки,
     * чтобы не дать выбрать точку, где нужных товаров нет (например, товар из другого города).
     * Пустая корзина → все винотеки доступны.
     */
    @Transactional(readOnly = true)
    public List<StoreAvailabilityDto> getStoreAvailability(UUID customerId) {
        BasketDto basket = getBasket(customerId);
        List<WineStore> stores = wineStoreRepository.findAll();

        Map<UUID, Integer> requestedByProduct = basket.getItems().stream()
                .collect(Collectors.toMap(it -> it.getProduct().getId(), BasketItemDto::getQuantity, Integer::sum));

        if (requestedByProduct.isEmpty()) {
            return stores.stream()
                    .map(store -> new StoreAvailabilityDto(store.getId(), true, List.of()))
                    .toList();
        }

        Map<UUID, String> nameByProduct = basket.getItems().stream()
                .collect(Collectors.toMap(it -> it.getProduct().getId(), it -> it.getProduct().getName(), (a, b) -> a));

        // storeId -> (productId -> остаток) одним запросом
        Map<Long, Map<UUID, Integer>> qtyByStore = productStockRepository.findByProduct_IdIn(requestedByProduct.keySet())
                .stream()
                .collect(Collectors.groupingBy(
                        stock -> stock.getWineStore().getId(),
                        Collectors.toMap(stock -> stock.getProduct().getId(), ProductStock::getQuantity)));

        return stores.stream().map(store -> {
            Map<UUID, Integer> available = qtyByStore.getOrDefault(store.getId(), Map.of());
            List<String> missing = requestedByProduct.entrySet().stream()
                    .filter(entry -> available.getOrDefault(entry.getKey(), 0) < entry.getValue())
                    .map(entry -> nameByProduct.get(entry.getKey()))
                    .toList();
            return new StoreAvailabilityDto(store.getId(), missing.isEmpty(), missing);
        }).toList();
    }

    // Получить корзину (если нет в Redis — создать пустую)
    public BasketDto getBasket(UUID customerId) {
        BasketDto basket = redisService.getObject(basketKey(customerId), BasketDto.class);
        if (basket != null) {
            return recalculateBasket(basket);
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
                            .productPhoto(ProductPhotoUrls.toFileDtos(product))
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
    public UUID checkoutBasket(UUID customerId, Long wineStoreId, CheckoutRequestDto checkoutData) {
        BasketDto basket = getBasket(customerId);
        Order order = orderService.createOrderFromBasket(basket, customerId, wineStoreId, checkoutData);
        clearBasket(customerId);
        return order.getId();
    }
}
