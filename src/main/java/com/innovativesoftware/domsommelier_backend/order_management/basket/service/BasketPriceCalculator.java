package com.innovativesoftware.domsommelier_backend.order_management.basket.service;

import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketItemDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Единственное место, где считаются деньги корзины и заказа.
 *
 * <p>Правила (зафиксированы с бизнесом, см. docs/DISCOUNTS_PLAN.md §4):
 * <ul>
 *   <li>эффективная цена позиции = акционная, если есть, иначе прайсовая;</li>
 *   <li>личная скидка <b>не складывается</b> с акцией — начисляется только на позиции
 *       без акционной цены (не уценяем уценённое);</li>
 *   <li>личная скидка и промокод <b>не складываются</b> — берётся большая из двух;</li>
 *   <li>округление — один раз, на итоговой сумме скидки, а не на каждой позиции,
 *       иначе сумма позиций не сойдётся с итогом.</li>
 * </ul>
 */
@Component
public class BasketPriceCalculator {

    private static final int SCALE = 2;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /**
     * Пересчитывает позиции и итоги корзины на месте.
     *
     * @param personalPercent действующая личная скидка клиента (0 — нет)
     */
    public BasketDto recalculate(BasketDto basket, int personalPercent) {
        List<BasketItemDto> items = basket.getItems() == null ? List.of() : basket.getItems();

        BigDecimal itemsTotal = BigDecimal.ZERO;       // по прайсу
        BigDecimal effectiveTotal = BigDecimal.ZERO;   // с учётом акций
        BigDecimal personalBase = BigDecimal.ZERO;     // только позиции без акции

        for (BasketItemDto item : items) {
            BigDecimal basePrice = nullToZero(item.getProduct().getPrice());
            BigDecimal salePrice = item.getProduct().getSalePrice();
            BigDecimal unitEffective = hasSale(salePrice, basePrice) ? salePrice : basePrice;
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity() == null ? 0 : item.getQuantity());

            BigDecimal lineTotal = unitEffective.multiply(quantity);
            item.setUnitEffectivePrice(unitEffective);
            item.setLineTotal(lineTotal);

            itemsTotal = itemsTotal.add(basePrice.multiply(quantity));
            effectiveTotal = effectiveTotal.add(lineTotal);
            if (!hasSale(salePrice, basePrice)) {
                personalBase = personalBase.add(lineTotal);
            }
        }

        BigDecimal saleDiscount = itemsTotal.subtract(effectiveTotal);
        BigDecimal personalDiscount = percentOf(personalBase, personalPercent);

        int promoPercent = basket.getPromoDiscountPercent() == null ? 0 : basket.getPromoDiscountPercent();
        BigDecimal promoDiscount = percentOf(effectiveTotal, promoPercent);

        // Личная скидка и промокод не складываются — применяем большую.
        BigDecimal appliedDiscount = personalDiscount.max(promoDiscount);

        basket.setItemsTotal(scaled(itemsTotal));
        basket.setSaleDiscountAmount(scaled(saleDiscount));
        basket.setPersonalDiscountPercent(personalPercent);
        basket.setPersonalDiscountAmount(scaled(personalDiscount));
        basket.setPromoDiscountPercent(promoPercent);
        basket.setPromoDiscountAmount(scaled(promoDiscount));
        basket.setTotalDiscountAmount(scaled(saleDiscount.add(appliedDiscount)));
        basket.setPayableTotal(scaled(effectiveTotal.subtract(appliedDiscount)));
        return basket;
    }

    /** Эффективная цена товара за штуку — та, по которой позиция уходит в заказ. */
    public BigDecimal effectiveUnitPrice(BigDecimal basePrice, BigDecimal salePrice) {
        BigDecimal base = nullToZero(basePrice);
        return hasSale(salePrice, base) ? salePrice : base;
    }

    /**
     * Акция считается заданной, только если цена положительная и ниже прайсовой:
     * {@code salePrice = 0} или «акция» дороже прайса — это данные, которым нельзя верить.
     */
    private boolean hasSale(BigDecimal salePrice, BigDecimal basePrice) {
        return salePrice != null && salePrice.signum() > 0 && salePrice.compareTo(basePrice) < 0;
    }

    private BigDecimal percentOf(BigDecimal amount, int percent) {
        if (percent <= 0 || amount.signum() <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(BigDecimal.valueOf(percent)).divide(HUNDRED, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal scaled(BigDecimal value) {
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
