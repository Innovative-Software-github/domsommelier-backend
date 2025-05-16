package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoCreateRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.ProductFilterValueDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/product-filter-values")
@RequiredArgsConstructor
public class ProductFilterValueController {

    private final ProductFilterValueService service;

    @GetMapping
    public ResponseEntity<List<ProductFilterValueDtoResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductFilterValueDtoResponse>> getByProductId(@PathVariable UUID productId) {
        return ResponseEntity.ok(service.getByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<ProductFilterValueDtoResponse> create(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                Метод для присвоения фильтров продукту.
                **Пример тела запроса для разных типов фильтров:**

                - Если тип **LIST** — укажите id продукта, фильтра и конкретное значение фильтра (id опции):
                  ```json
                  {
                    "productId": "293b85d0-f739-4856-a09b-8495f2157e4d",
                    "filterId": "7e28e572-df42-467a-b6ee-5098ce07048b",
                    "filterOptionId": "4d0c40ec-200e-48c1-9680-413281b73d39"
                  }
                  ```
                  Можно указать конкретное значение опции в value, не указывая filterOptionId:
                  ```json
                  {
                    "productId": "293b85d0-f739-4856-a09b-8495f2157e4d",
                    "filterId": "7e28e572-df42-467a-b6ee-5098ce07048b",
                    "value": "string1"
                  }

                - Если тип **RANGE** — укажите id продукта, фильтра и конкретное значение фильтра
                (числовое значение, по нему будет осуществляться поиск диапазонного значения):
                  ```json
                  {
                    "productId": "293b85d0-f739-4856-a09b-8495f2157e4d",
                    "filterId": "7e28e572-df42-467a-b6ee-5098ce07048b",
                    "value": "999.99"
                  }
                  ```
                """
            ) ProductFilterValueDtoCreateRequest dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductFilterValueDtoResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductFilterValueDtoResponse> update(
            @PathVariable UUID id, @RequestBody ProductFilterValueDtoRequest dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public UUID delete(@PathVariable UUID id) { return service.delete(id); }

    @PostMapping("/filter")
    public ResponseEntity<List<UUID>> getByFilterIdAndFilterOptionId(
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                Метод для получения по фильтру. Возвращает список UUID продуктов.

                **Список фильтров в формате:**

                - Если тип **LIST**:
                  ```json
                  {
                    "Название фильтра (name или field или UUID, не имеет значения)": ["Опция1", "Опция2"]
                  }
                  ```

                - Если тип **RANGE**:
                  ```json
                  {
                    "Название фильтра (name или field или UUID, не имеет значения)": ["Нижняя граница", "Верхняя граница"]
                  }
                  ```
                """
            ) Map<String, List<String>> params
    ) {
        return ResponseEntity.ok(service.getAllByFilterIdAndFilterOptionId(params));
    }
}
