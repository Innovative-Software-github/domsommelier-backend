package com.innovativesoftware.domsommelier_backend.product.service;

import com.innovativesoftware.domsommelier_backend.product.model.WineDTO;
import com.innovativesoftware.domsommelier_backend.product.repository.ProductPhotoRepository;
import com.innovativesoftware.domsommelier_backend.product.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class WineService {
    @Autowired
    private WineRepository wineRepository;

    @Autowired
    private ProductPhotoRepository productPhotoRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public List<WineDTO> findAllWines() {
        return wineRepository.findAllWines().stream()
                .map(wine -> modelMapper.map(wine, WineDTO.class))
                .toList();
    }
}
