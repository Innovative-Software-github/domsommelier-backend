package com.innovativesoftware.domsommelier_backend.admin_management.dashboard.service;

import com.innovativesoftware.domsommelier_backend.admin_management.dashboard.model.AdminDashboardStatsDto;
import com.innovativesoftware.domsommelier_backend.event_management.event.repository.EventRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.store.repository.WineStoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final EventRepository eventRepository;
    private final WineStoreRepository wineStoreRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStatsDto getStats() {
        return AdminDashboardStatsDto.builder()
                .newOrdersCount(orderRepository.countByOrderStatus_Name("NEW"))
                .productsCount(productRepository.count())
                .upcomingEventsCount(eventRepository.countByDatetimeAfter(OffsetDateTime.now()))
                .wineStoresCount(wineStoreRepository.count())
                .build();
    }
}
