package com.innovativesoftware.domsommelier_backend.filter_management.controller;

import com.innovativesoftware.domsommelier_backend.filter_management.service.FilterService;
import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterValueService;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "UserUiFilterController", description = "Контроллер управления фильтрами для пользователя (поиск)")
@RequestMapping("/api/v1/user/filters")
@RequiredArgsConstructor
public class UserUiFilterController {
    private final FilterService filterService;
    private final ProductFilterValueService productFilterValueService;

    @GetMapping("/productCategory")
    @Operation(summary = "Получение всех фильтров по категории продукта")
    public ResponseEntity<List<UUID>> getFiltersByProductCategory(
            @RequestParam("productCategoryId") ProductCategories productCategory
    ) {
        return ResponseEntity.ok(filterService.getFiltersByProductCategory(productCategory));
    }

    @GetMapping("/filterTypes")
    @Operation(summary = "Получение всех типов фильтров")
    public ResponseEntity<List<String>> getFilterTypes() {
        return ResponseEntity.ok(filterService.getFilterTypes());
    }

    @PostMapping("/filter")
    @Operation(summary = "Получение продуктов по фильтру")
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
        return ResponseEntity.ok(productFilterValueService.getAllByFilterIdAndFilterOptionId(params));
    }
}
