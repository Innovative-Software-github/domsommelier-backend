package com.innovativesoftware.domsommelier_backend.product_management.product.util;

import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
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

    public LinkedHashMap<String, List<FileDTO>> getProductToPhotoMap(ProductCategories productCategory) {
        LinkedHashMap<String, List<FileDTO>> productToPhoto = new LinkedHashMap<>();
        List<FileDTO> productPhotos = productPhotoRepository.findAllProductPhotosFor(productCategory);
        for (FileDTO photo : productPhotos) {
            String photoKey = photo.getId().toString();
            if (!productToPhoto.containsKey(photoKey)) {
                List<FileDTO> photos = new ArrayList<>();
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
