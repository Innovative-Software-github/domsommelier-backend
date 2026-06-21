package com.innovativesoftware.domsommelier_backend.geo_management.city.controller;

import com.innovativesoftware.domsommelier_backend.geo_management.city.model.CityDto;
import com.innovativesoftware.domsommelier_backend.geo_management.city.model.CityRequestDto;
import com.innovativesoftware.domsommelier_backend.geo_management.city.service.CityService;
import com.innovativesoftware.domsommelier_backend.infrastructure.security.RequiresAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
@Tag(name = "Cities", description = "Справочник городов присутствия")
public class CityController {

    private final CityService cityService;

    @GetMapping
    @Operation(summary = "Активные города (для витрины)")
    public List<CityDto> getActiveCities() {
        return cityService.getActiveCities();
    }

    @GetMapping("/all")
    @RequiresAdmin
    @Operation(summary = "Все города, включая неактивные (для админки)")
    public List<CityDto> getAllCities() {
        return cityService.getAllCities();
    }

    @PostMapping
    @RequiresAdmin
    @Operation(summary = "Создать город")
    public ResponseEntity<CityDto> create(@RequestBody @Valid CityRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cityService.create(request));
    }

    @PutMapping("/{id}")
    @RequiresAdmin
    @Operation(summary = "Обновить город")
    public CityDto update(@PathVariable Long id, @RequestBody @Valid CityRequestDto request) {
        return cityService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @RequiresAdmin
    @Operation(summary = "Удалить город")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
