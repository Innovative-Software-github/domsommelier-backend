package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.model.types.FilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.service.FilterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/filters")
@RequiredArgsConstructor
public class FilterController {
    private final FilterService filterService;

    @GetMapping
    public ResponseEntity<List<FilterDto>> getAll() {
        return ResponseEntity.ok(filterService.getAllFilters());
    }

    @PostMapping
    public ResponseEntity<UUID> create(
            @RequestBody HashMap<String, Object> filterDTO
    ) {
        return ResponseEntity.ok(filterService.create(filterDTO));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<FilterDto> getByName(@PathVariable String name) {
        return ResponseEntity.ok(filterService.getByName(name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilterDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(filterService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilterDto> update(@PathVariable UUID id, @RequestBody Map<String, Object> filterDTO) {
        return ResponseEntity.ok(filterService.update(id, filterDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(filterService.delete(id));
    }
}
