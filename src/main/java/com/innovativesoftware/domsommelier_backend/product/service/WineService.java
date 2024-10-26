package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product.model.ProductPhotoProjection;
import com.innovativesoftware.domsommelier_backend.product.model.WineDTO;
import com.innovativesoftware.domsommelier_backend.product.model.WineWithPhotoDTO;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product.repository.WineRepository;
import com.innovativesoftware.domsommelier_backend.product.util.ProductToPhotoUtil;
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

    @Autowired
    private ProductToPhotoUtil productToPhotoUtil;

    private ModelMapper modelMapper = new ModelMapper();

    public List<WineWithPhotoDTO> findAllWines() {

        List<WineDTO> wines = wineRepository.findAllWines().stream()
                .map(wine -> modelMapper.map(wine, WineDTO.class))
                .toList();

        LinkedHashMap<String, List<ProductPhotoProjection>> productToPhoto = productToPhotoUtil.getProductToPhotoMap(ProductCategories.WINE);

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
