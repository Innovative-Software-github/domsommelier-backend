package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.model.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.storagehistory.repository.StorageHistoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductNewService {

    @Autowired
    private StorageHistoryRepository storageHistoryRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public List<ProductNewDTO> getNewProducts() {
        List<ProductNewDTO> newProducts = storageHistoryRepository.findNewProductsInStorageHistory()
                .stream()
                .map(productNewProjection -> modelMapper.map(productNewProjection, ProductNewDTO.class))
                .toList();
        return newProducts;
    }
}
