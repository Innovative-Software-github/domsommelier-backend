package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.DTO.ProductNewDTO;
import com.innovativesoftware.domsommelier_backend.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ProductNewService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductNewDTO> getNewProducts() {
        OffsetDateTime recentTime = OffsetDateTime.now().minusDays(5);
        List<Product> newProducts = productRepository.findByCreatedAtGreaterThanAndQuantityGreaterThan(recentTime, 0);
        return newProducts.stream()
                .map(product -> {
                    ProductNewDTO productNewDTO = new ProductNewDTO();
                    productNewDTO.setId(product.getId());
                    return productNewDTO;
                })
                .toList();
    }
}
