package com.innovativesoftware.domsommelier_backend.product_management.store.service;

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
        WineStore store = WineStore.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .workingHours(requestDto.getWorkingHours())
                .city(requestDto.getCity())
                .district(requestDto.getDistrict())
                .location(new WineStorePoint(requestDto.getLongitude(), requestDto.getLatitude()))
                .build();
        return wineStoreMapper.toResponseDto(wineStoreRepository.save(store));
    }

    @Override
    @Transactional
    public WineStoreResponseDto updateWineStore(Long id, WineStoreRequestDto requestDto) {
        WineStore store = wineStoreRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Винотека не найдена: " + id));
        store.setName(requestDto.getName());
        store.setAddress(requestDto.getAddress());
        store.setPhone(requestDto.getPhone());
        store.setWorkingHours(requestDto.getWorkingHours());
        store.setCity(requestDto.getCity());
        store.setDistrict(requestDto.getDistrict());
        store.setLocation(new WineStorePoint(requestDto.getLongitude(), requestDto.getLatitude()));
        return wineStoreMapper.toResponseDto(wineStoreRepository.save(store));
    }

    @Override
    @Transactional
    public void deleteWineStore(Long id) {
        if (!wineStoreRepository.existsById(id)) {
            throw new EntityNotFoundException("Винотека не найдена: " + id);
        }
        wineStoreRepository.deleteById(id);
    }
}
