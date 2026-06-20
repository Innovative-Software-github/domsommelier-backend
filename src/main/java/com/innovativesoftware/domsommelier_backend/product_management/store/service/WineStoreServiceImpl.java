package com.innovativesoftware.domsommelier_backend.product_management.store.service;

import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.order_management.order.repository.OrderRepository;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStorePoint;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreFilterDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreRequestDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreResponseDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.repository.WineStoreRepository;
import com.innovativesoftware.domsommelier_backend.product_management.store.utils.WineStoreMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WineStoreServiceImpl implements WineStoreService {

    private final WineStoreRepository wineStoreRepository;
    private final WineStoreMapper wineStoreMapper;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Page<WineStoreResponseDto> getAllWineStores(
            Integer page, Integer size, WineStoreFilterDto filter
    ) {
        Specification<WineStore> specification = WineStoreSpecification.withFilter(filter);
        Pageable pageable = PageRequest.of(page, size);
        Page<WineStore> wineStores = wineStoreRepository.findAll(specification, pageable);
        return wineStores.map(wineStoreMapper::toResponseDto);
    }

    @Override
    public WineStoreResponseDto getWineStoreById(Long id) {
        WineStore store = wineStoreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Винотека не найдена: " + id));
        return wineStoreMapper.toResponseDto(store);
    }

    @Override
    @Transactional
    public WineStoreResponseDto createWineStore(WineStoreRequestDto requestDto) {
        WineStore store = WineStore.builder().build();
        applyRequest(store, requestDto);
        return wineStoreMapper.toResponseDto(wineStoreRepository.save(store));
    }

    @Override
    @Transactional
    public WineStoreResponseDto updateWineStore(Long id, WineStoreRequestDto requestDto) {
        WineStore store = wineStoreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Винотека не найдена: " + id));
        applyRequest(store, requestDto);
        return wineStoreMapper.toResponseDto(wineStoreRepository.save(store));
    }

    @Override
    @Transactional
    public void deleteWineStore(Long id) {
        if (!wineStoreRepository.existsById(id)) {
            throw new EntityNotFoundException("Винотека не найдена: " + id);
        }
        if (orderRepository.existsByWineStore_Id(id)) {
            throw new IllegalStateException("Невозможно удалить винотеку: есть связанные заказы");
        }
        if (customerRepository.existsByDefaultWineStore_Id(id)) {
            throw new IllegalStateException("Невозможно удалить винотеку: она привязана к клиентам");
        }
        wineStoreRepository.deleteById(id);
    }

    private void applyRequest(WineStore store, WineStoreRequestDto requestDto) {
        store.setName(requestDto.getName().trim());
        store.setAddress(trimToNull(requestDto.getAddress()));
        store.setPhone(trimToNull(requestDto.getPhone()));
        store.setWorkingHours(trimToNull(requestDto.getWorkingHours()));
        store.setCity(normalizeLocationText(requestDto.getCity()));
        store.setDistrict(normalizeLocationText(requestDto.getDistrict()));
        store.setLocation(new WineStorePoint(requestDto.getLongitude(), requestDto.getLatitude()));
    }

    private static String normalizeLocationText(String value) {
        return value.trim().toLowerCase();
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
