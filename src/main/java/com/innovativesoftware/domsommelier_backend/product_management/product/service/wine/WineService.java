package com.innovativesoftware.domsommelier_backend.product_management.product.service.wine;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.wine.WineProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.wine.WineWithPhotosDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.util.ProductToPhotoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WineService {
    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    @Autowired
    private ProductToPhotoUtil productToPhotoUtil;

    public List<WineWithPhotosDTO> findAllWines() {

        List<WineProjection> wines = wineRepository.findAllWines();

        LinkedHashMap<String, List<FileDTO>> productToPhoto = productToPhotoUtil.getProductToPhotoMap(ProductCategoryEnum.wine);

        return wines.stream()
                .map(wine -> new WineWithPhotosDTO()
                        .setFiles(productToPhoto.get(wine.getId().toString()))
                        .setId(wine.getId())
                        .setDiscount(wine.getDiscount())
                        .setName(wine.getName())
                        .setPrice(wine.getPrice())
                )
                .toList();
    }

    public List<WineWithPhotosDTO> findWinesByCountry(String country) {
        List<WineProjection> winesForCountry = wineRepository.findWinesByCountry(country);
        LinkedHashMap<String, List<FileDTO>> productToPhoto = productToPhotoUtil.getProductToPhotoMap(ProductCategoryEnum.wine);
        return winesForCountry.stream()
                .map(wine -> new WineWithPhotosDTO()
                        .setFiles(productToPhoto.get(wine.getId().toString()))
                        .setId(wine.getId())
                        .setDiscount(wine.getDiscount())
                        .setName(wine.getName())
                        .setPrice(wine.getPrice())
                )
                .toList();
    }
}
