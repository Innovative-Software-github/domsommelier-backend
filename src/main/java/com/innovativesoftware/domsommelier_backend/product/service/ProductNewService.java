package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.storagehistory.repository.StorageHistoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductNewService {

    @Autowired
    private StorageHistoryRepository storageHistoryRepository;

    @Autowired
    private CacheManager cacheManager;

    private final String NEW_PRODUCTS_CACHE_NAME = "new_products";

    private ModelMapper modelMapper = new ModelMapper();

    @Scheduled(fixedRate = 30000)
    private void runNewProducts() {
        Cache cache = cacheManager.getCache("newProductsCache");

        List<ProductNewDTO> newProducts = storageHistoryRepository.findNewProductsInStorageHistory()
                .stream()
                .map(productNewProjection -> modelMapper.map(productNewProjection, ProductNewDTO.class))
                .toList();

        cache.put(NEW_PRODUCTS_CACHE_NAME, newProducts);
    }

    public List<ProductNewDTO> getNewProducts() {
        Cache cache = cacheManager.getCache("newProductsCache");
        if (cache == null) {
            return null;
        }
        return cache.get(NEW_PRODUCTS_CACHE_NAME, List.class);
    }
}
