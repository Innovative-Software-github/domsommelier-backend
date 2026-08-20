package com.innovativesoftware.domsommelier_backend.admin_management.dashboard.model;

import lombok.Builder;
import lombok.Data;

/**
 * Сводные цифры для главной страницы админки (карточки «Заказы»/«Товары»/«Мероприятия»/«Винотеки»).
 */
@Data
@Builder
public class AdminDashboardStatsDto {
    /** Заказы в статусе NEW — требуют внимания. */
    private long newOrdersCount;
    /** Товары во всех категориях каталога суммарно. */
    private long productsCount;
    /** Мероприятия с датой в будущем (прошедшие не считаем). */
    private long upcomingEventsCount;
    /** Точки самовывоза (винотеки) суммарно. */
    private long wineStoresCount;
}
