package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/filters")
@RequiredArgsConstructor
public class FilterController {
    private final FilterService filterService;

    @GetMapping
    public ResponseEntity<List<FilterDtoResponse>> getAll() {
        return ResponseEntity.ok(filterService.getAllFilters());
    }

    @PostMapping
    public ResponseEntity<FilterDtoResponse> create(@RequestBody FilterDtoRequest filterDTO) {
        return ResponseEntity.ok(filterService.create(filterDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilterDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(filterService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilterDtoResponse> update(@PathVariable UUID id, @RequestBody FilterDtoRequest filterDTO) {
        return ResponseEntity.ok(filterService.update(id, filterDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(filterService.delete(id));
    }
}
