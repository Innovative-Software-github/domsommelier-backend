package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterOptionDtoCreateRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterOptionDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterOptionDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.service.FilterOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/filter-options")
@RequiredArgsConstructor
public class FilterOptionController {
    private final FilterOptionService service;

    @GetMapping
    public ResponseEntity<List<FilterOptionDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    public ResponseEntity<FilterOptionDtoResponse> create(@RequestBody FilterOptionDtoCreateRequest filterOptionDTO) {
        return ResponseEntity.ok(service.create(filterOptionDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilterOptionDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/filter/{filterId}")
    public ResponseEntity<List<FilterOptionDtoResponse>> getByFilterId(@PathVariable UUID filterId) {
        return ResponseEntity.ok(service.getByFilterId(filterId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilterOptionDtoResponse> update(
            @PathVariable UUID id, @RequestBody FilterOptionDtoRequest filterOptionDTO) {
        return ResponseEntity.ok(service.update(id, filterOptionDTO));
    }

    @DeleteMapping("/{id}")
    public UUID delete(@PathVariable UUID id) {
        return service.delete(id);
    }
}
