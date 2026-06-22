package com.innovativesoftware.domsommelier_backend.order_management.basket.model;

import java.util.List;

/**
 * Доступность текущей корзины в конкретной винотеке.
 *
 * @param wineStoreId         id винотеки
 * @param available           true, если все позиции корзины есть в этой винотеке в нужном количестве
 * @param unavailableProducts названия позиций, которых не хватает (для подсказки в UI)
 */
public record StoreAvailabilityDto(
        Long wineStoreId,
        boolean available,
        List<String> unavailableProducts
) {
}
