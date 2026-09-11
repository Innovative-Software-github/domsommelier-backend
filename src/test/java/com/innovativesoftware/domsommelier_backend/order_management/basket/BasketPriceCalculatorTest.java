package com.innovativesoftware.domsommelier_backend.order_management.basket;

import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.model.BasketItemDto;
import com.innovativesoftware.domsommelier_backend.order_management.basket.service.BasketPriceCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Правила расчёта скидок (docs/DISCOUNTS_PLAN.md §4). Тест фиксирует именно договорённости,
 * а не реализацию: личная скидка не складывается ни с акцией, ни с промокодом.
 */
class BasketPriceCalculatorTest {

    private final BasketPriceCalculator calculator = new BasketPriceCalculator();

    @Test
    @DisplayName("Без скидок к оплате — сумма по прайсу")
    void noDiscounts() {
        BasketDto basket = basketOf(item("3000.00", null, 2));

        calculator.recalculate(basket, 0);

        assertThat(basket.getItemsTotal()).isEqualByComparingTo("6000.00");
        assertThat(basket.getSaleDiscountAmount()).isEqualByComparingTo("0.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("6000.00");
    }

    @Test
    @DisplayName("Личная скидка применяется к товарам без акции")
    void personalDiscountOnRegularItem() {
        BasketDto basket = basketOf(item("3000.00", null, 2));

        calculator.recalculate(basket, 10);

        assertThat(basket.getPersonalDiscountAmount()).isEqualByComparingTo("600.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("5400.00");
    }

    @Test
    @DisplayName("Личная скидка НЕ начисляется на товар с акционной ценой")
    void personalDiscountNotStackedWithSale() {
        BasketDto basket = basketOf(item("3000.00", "2000.00", 1));

        calculator.recalculate(basket, 10);

        assertThat(basket.getSaleDiscountAmount()).isEqualByComparingTo("1000.00");
        assertThat(basket.getPersonalDiscountAmount()).isEqualByComparingTo("0.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("2000.00");
    }

    @Test
    @DisplayName("В смешанной корзине скидка идёт только на неакционную часть")
    void mixedBasket() {
        BasketDto basket = basketOf(
                item("3000.00", null, 1),        // 3000 — под личную скидку
                item("2000.00", "1500.00", 1));  // акция, личная скидка не применяется

        calculator.recalculate(basket, 10);

        assertThat(basket.getItemsTotal()).isEqualByComparingTo("5000.00");
        assertThat(basket.getSaleDiscountAmount()).isEqualByComparingTo("500.00");
        assertThat(basket.getPersonalDiscountAmount()).isEqualByComparingTo("300.00");
        assertThat(basket.getTotalDiscountAmount()).isEqualByComparingTo("800.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("4200.00");
    }

    @Test
    @DisplayName("Промокод и личная скидка не складываются — берётся большая")
    void promoAndPersonalTakeMax() {
        BasketDto basket = basketOf(item("1000.00", null, 1));
        basket.setPromoDiscountPercent(20);

        calculator.recalculate(basket, 5);

        assertThat(basket.getPersonalDiscountAmount()).isEqualByComparingTo("50.00");
        assertThat(basket.getPromoDiscountAmount()).isEqualByComparingTo("200.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("800.00");
    }

    @Test
    @DisplayName("«Акция» дороже прайса игнорируется — это мусор в данных")
    void saleAboveBasePriceIgnored() {
        BasketDto basket = basketOf(item("1000.00", "1500.00", 1));

        calculator.recalculate(basket, 10);

        assertThat(basket.getSaleDiscountAmount()).isEqualByComparingTo("0.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("900.00");
    }

    @Test
    @DisplayName("Сумма позиций сходится с итогом при копеечном округлении")
    void roundingKeepsTotalsConsistent() {
        BasketDto basket = basketOf(item("333.33", null, 3));

        calculator.recalculate(basket, 15);

        assertThat(basket.getItemsTotal()).isEqualByComparingTo("999.99");
        assertThat(basket.getPersonalDiscountAmount()).isEqualByComparingTo("150.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("849.99");
        assertThat(basket.getItems().get(0).getLineTotal()).isEqualByComparingTo("999.99");
    }

    @Test
    @DisplayName("Пустая корзина — нули, а не NPE")
    void emptyBasket() {
        BasketDto basket = basketOf();

        calculator.recalculate(basket, 10);

        assertThat(basket.getItemsTotal()).isEqualByComparingTo("0.00");
        assertThat(basket.getPayableTotal()).isEqualByComparingTo("0.00");
    }

    private BasketDto basketOf(BasketItemDto... items) {
        BasketDto basket = new BasketDto();
        basket.setCustomerId(UUID.randomUUID());
        basket.setItems(new ArrayList<>(List.of(items)));
        basket.setPromoDiscountPercent(0);
        return basket;
    }

    private BasketItemDto item(String price, String salePrice, int quantity) {
        BasketItemDto.BasketItemDtoIdClass product = new BasketItemDto.BasketItemDtoIdClass();
        product.setId(UUID.randomUUID());
        product.setPrice(new BigDecimal(price));
        product.setSalePrice(salePrice == null ? null : new BigDecimal(salePrice));

        BasketItemDto item = new BasketItemDto();
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }
}
