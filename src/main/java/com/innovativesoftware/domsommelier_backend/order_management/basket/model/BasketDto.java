package com.innovativesoftware.domsommelier_backend.order_management.basket.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Корзина со всеми посчитанными суммами. Все расчёты делает {@code BasketPriceCalculator}
 * на бэкенде — клиент только отображает готовые значения и никогда не умножает на проценты.
 *
 * <p>Поля {@code totalPrice}/{@code discount}/{@code discountedPrice} — алиасы старого контракта,
 * оставлены на один релиз: корзины лежат в Redis, и на прод одновременно приходят старые корзины
 * с новым кодом и новый бэкенд со старым фронтом.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDto implements Serializable {
    private UUID customerId;
    @Singular
    private List<BasketItemDto> items;

    /** Сумма позиций по прайсу, без скидок. */
    @Builder.Default
    private BigDecimal itemsTotal = BigDecimal.ZERO;
    /** Экономия за счёт акционных цен товаров. */
    @Builder.Default
    private BigDecimal saleDiscountAmount = BigDecimal.ZERO;
    /** Действующий процент личной скидки клиента (0 — скидки нет). */
    @Builder.Default
    private Integer personalDiscountPercent = 0;
    /** Личная скидка в рублях. Не начисляется на позиции с акционной ценой. */
    @Builder.Default
    private BigDecimal personalDiscountAmount = BigDecimal.ZERO;
    /** Процент промокода, применённого к корзине (0 — промокода нет). */
    @Builder.Default
    private Integer promoDiscountPercent = 0;
    /** Промокод в рублях. С личной скидкой не складывается — берётся большая из двух. */
    @Builder.Default
    private BigDecimal promoDiscountAmount = BigDecimal.ZERO;
    /** Итоговая экономия: акции + большая из (личная скидка, промокод). */
    @Builder.Default
    private BigDecimal totalDiscountAmount = BigDecimal.ZERO;
    /** Сумма к оплате. */
    @Builder.Default
    private BigDecimal payableTotal = BigDecimal.ZERO;

    /** @deprecated старое имя, равно {@link #itemsTotal}. */
    @Deprecated
    public BigDecimal getTotalPrice() {
        return itemsTotal;
    }

    /** @deprecated старое имя, равно {@link #payableTotal}. */
    @Deprecated
    public BigDecimal getDiscountedPrice() {
        return payableTotal;
    }

    /** @deprecated раньше здесь лежал процент промокода. */
    @Deprecated
    public Integer getDiscount() {
        return promoDiscountPercent;
    }

    /** @deprecated для чтения корзин старого формата из Redis. */
    @Deprecated
    public void setDiscount(Integer discount) {
        this.promoDiscountPercent = discount;
    }
}
