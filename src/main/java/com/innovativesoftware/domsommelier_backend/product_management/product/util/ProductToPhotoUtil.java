package com.innovativesoftware.domsommelier_backend.product_management.product.util;

import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductPhotoProjection;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class ProductToPhotoUtil {

    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    public LinkedHashMap<String, List<ProductPhotoProjection>> getProductToPhotoMap(ProductCategories productCategory) {
        LinkedHashMap<String, List<ProductPhotoProjection>> productToPhoto = new LinkedHashMap<>();
        List<ProductPhotoProjection> productPhotos = productPhotoRepository.findAllProductPhotosFor(productCategory);
        for (ProductPhotoProjection photo : productPhotos) {
            String photoKey = photo.getId().toString();
            if (!productToPhoto.containsKey(photoKey)) {
                List<ProductPhotoProjection> photos = new ArrayList<>();
                photos.add(photo);
                productToPhoto.put(photo.getId().toString(), photos);
            }
            else {
                productToPhoto.get(photoKey).add(photo);
            }
        }
        return productToPhoto;
    }
}
