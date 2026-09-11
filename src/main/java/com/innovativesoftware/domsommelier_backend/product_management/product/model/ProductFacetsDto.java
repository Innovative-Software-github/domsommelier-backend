package com.innovativesoftware.domsommelier_backend.product_management.product.model;

import java.util.Map;

/**
 * Фасеты каталога для текущего выбора фильтров.
 *
 * @param total   сколько товаров даст весь текущий выбор
 * @param options для каждого multi_select-фильтра: подпись варианта → сколько товаров
 *                будет, если его отметить (с учётом остальных фильтров, но не этого же)
 */
public record ProductFacetsDto(long total, Map<String, Map<String, Long>> options) {
}
