package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Справочные значения для формы товара. Заполняется по категории:
 * страны — всегда; цвета/типы — для вина (для остальных категорий добавляются по мере реализации).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReferenceDto {
    private List<String> countries;
    private List<String> colors;         // вино, игристое
    private List<String> types;          // вино
    private List<String> subcategories;  // крепкий, игристое, слабоалкогольное, снеки
    private List<String> sugarContents;  // игристое
}
