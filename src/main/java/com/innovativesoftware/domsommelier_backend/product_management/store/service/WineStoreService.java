package com.innovativesoftware.domsommelier_backend.product_management.store.service;

import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreFilterDto;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreResponseDto;
import org.springframework.data.domain.Page;

public interface WineStoreService {
    Page<WineStoreResponseDto> getAllWineStores(Integer page, Integer size, WineStoreFilterDto filter);
}
