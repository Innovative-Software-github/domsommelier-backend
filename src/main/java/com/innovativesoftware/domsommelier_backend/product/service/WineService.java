package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product.model.ProductPhotoDTO;
import com.innovativesoftware.domsommelier_backend.product.model.WineDTO;
import com.innovativesoftware.domsommelier_backend.product.model.WineWithPhotoDTO;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

    private ModelMapper modelMapper = new ModelMapper();

    public List<WineWithPhotoDTO> findAllWines() {

        List<WineDTO> wines = wineRepository.findAllWines().stream()
                .map(wine -> modelMapper.map(wine, WineDTO.class))
                .toList();

        // generic photos to "product to photo" hashmap
        LinkedHashMap<String, List<ProductPhotoDTO>> productToPhoto = new LinkedHashMap<>();
        List<ProductPhotoDTO> productPhotos = productPhotoRepository.findAllProductPhotosFor(ProductCategories.WINE).stream()
                .map(photo -> modelMapper.map(photo, ProductPhotoDTO.class))
                .toList();
        for (ProductPhotoDTO photo : productPhotos) {
            String photoKey = photo.getId().toString();
            if (!productToPhoto.containsKey(photoKey)) {
                List<ProductPhotoDTO> photos = new ArrayList<>();
                photos.add(photo);
                productToPhoto.put(photo.getId().toString(), photos);
            }
            else {
                productToPhoto.get(photoKey).add(photo);
            }
        }
        //

       return wines.stream()
                .map(wine -> {
                    WineWithPhotoDTO wineWithPhotoDTO = new WineWithPhotoDTO();
                    wineWithPhotoDTO.setId(wine.getId());
                    wineWithPhotoDTO.setDiscount(wine.getDiscount());
                    wineWithPhotoDTO.setName(wine.getName());
                    wineWithPhotoDTO.setPrice(wine.getPrice());
                    wineWithPhotoDTO.setProductPhotos(productToPhoto.get(wine.getId().toString()));
                    return wineWithPhotoDTO;
                })
                .toList();

    }
}
