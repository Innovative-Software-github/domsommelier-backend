package com.innovativesoftware.domsommelier_backend.product_management.store.service;

import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreFilterDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreResponseDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.repository.WineStoreRepository;
import com.innovativesoftware.domsommelier_backend.product_management.store.utils.WineStoreMapper;
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
}
