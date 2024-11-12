package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductPhotoProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.WineProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.WineWithPhotoDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductToPhotoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class WineService {
    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    @Autowired
    private ProductToPhotoUtil productToPhotoUtil;

    public List<WineWithPhotoDTO> findAllWines() {

        List<WineProjection> wines = wineRepository.findAllWines();

        LinkedHashMap<String, List<ProductPhotoProjection>> productToPhoto = productToPhotoUtil.getProductToPhotoMap(ProductCategories.WINE);

        return wines.stream()
                .map(wine -> new WineWithPhotoDTO()
                        .setProductPhotos(productToPhoto.get(wine.getId().toString()))
                        .setId(wine.getId())
                        .setDiscount(wine.getDiscount())
                        .setName(wine.getName())
                        .setPrice(wine.getPrice())
                )
                .toList();
    }

    public List<WineWithPhotoDTO> findWinesByCountry(String country) {
        List<WineProjection> winesForCountry = wineRepository.findWinesByCountry(country);
        LinkedHashMap<String, List<ProductPhotoProjection>> productToPhoto = productToPhotoUtil.getProductToPhotoMap(ProductCategories.WINE);
        return winesForCountry.stream()
                .map(wine -> new WineWithPhotoDTO()
                        .setProductPhotos(productToPhoto.get(wine.getId().toString()))
                        .setId(wine.getId())
                        .setDiscount(wine.getDiscount())
                        .setName(wine.getName())
                        .setPrice(wine.getPrice())
                )
                .toList();
    }
}
