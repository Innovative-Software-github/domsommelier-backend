package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoCreateRequest;
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
    public ResponseEntity<FilterDtoResponse> create(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                Метод для создания фильтра.  
                Возвращает id созданного фильтра.

                **Пример тела запроса для разных типов фильтров:**

                - Если тип **LIST** — укажите перечень допустимых значений:
                  ```json
                  {
                    "name": "string",
                    "field": "string",
                    "productCategory": "WINE",
                    "filterType": "LIST",
                    "options": [
                      "string1", "string2", "string3"
                    ]
                  }
                  ```

                - Если тип **RANGE** — два значения (границы диапазона; порядок не важен, будет обработан на бэке):
                  ```json
                  {
                    "name": "string",
                    "field": "string",
                    "productCategory": "WINE",
                    "filterType": "RANGE",
                    "options": [
                      "left", "right"
                    ]
                  }
                  ```

                - Если тип **STRING** — одно значение (поисковая строка):
                  ```json
                  {
                    "name": "string",
                    "field": "string",
                    "productCategory": "WINE",
                    "filterType": "STRING",
                    "options": [
                      "string"
                    ]
                  }
                  ```

                - Обязательные поля: `name`, `field`, `productCategory`, `filterType`, `options`
                - Поле `options`:
                  - Для **LIST** — список возможных значений
                  - Для **RANGE** — две границы диапазона (числа или строки)
                  - Для **STRING** — только одно значение

                """
            )
            FilterDtoCreateRequest filterDTO
    ) {
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
