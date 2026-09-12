package com.innovativesoftware.domsommelier_backend.filter_management.catalog;

import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.*;
import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.filter_management.service.ProductFilterFieldProvider;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Read-time configuration: only values actually assigned to catalog products are offered. */
@Service @RequiredArgsConstructor
public class CatalogFilterConfigService {
    private final JdbcTemplate jdbc;
    private final List<ProductFilterFieldProvider> providers;
    private static final Map<String, String> TABLES = Map.of("wine", "wine", "champagne_and_sparkling", "sparkling_wine",
        "spirit", "spirit", "low_alcohol", "low_alcohol", "snack", "gourmet_product", "accessories", "accessories_product");

    public Map<String, FilterDto> enrich(ProductCategoryEnum category, Collection<FilterDto> existing) {
        Map<String, FilterDto> result = new LinkedHashMap<>();
        var provider = providers.stream().filter(p -> p.getSupportedCategory() == category).findFirst();
        for (FilterDto filter : existing) {
            if (filter instanceof MultiSelectFilterDto multi && provider.isPresent()
                    && provider.get().getFieldRuNames().containsKey(filter.getField())) {
                multi.setOptions(provider.get().getLabels(filter.getField()).stream().filter(Objects::nonNull)
                    .filter(label -> !label.isBlank()).sorted(String.CASE_INSENSITIVE_ORDER)
                    .map(label -> new MultiSelectFilterDto.Option(label, label)).toArray(MultiSelectFilterDto.Option[]::new));
            }
            if (filter instanceof MultiSelectFilterDto multi && (multi.getOptions() == null || multi.getOptions().length == 0)) continue;
            if (filter instanceof RangeFilterDto range) {
                String expression = switch (filter.getField()) {
                    case "price" -> "p.price";
                    case "year" -> category == ProductCategoryEnum.wine ? "t.production_year" : null;
                    case "strength" -> Set.of("spirit", "low_alcohol").contains(category.name()) ? "t.strength" : null;
                    default -> null;
                };
                if (expression != null && !setBounds(range, expression, from(category))) continue;
            }
            result.put(filter.getField(), filter);
        }
        if (category == ProductCategoryEnum.wine) {
            var year = RangeFilterDto.builder().id(UUID.nameUUIDFromBytes("wine:year".getBytes(StandardCharsets.UTF_8)))
                .category(category).field("year").name("Год урожая").type(FilterType.range).unit("").steps(new RangeFilterDto.Step[0]).build();
            if (setBounds(year, "t.production_year", from(category))) result.put("year", year);
            var grapes = jdbc.query("SELECT DISTINCT g.grape, r.label FROM wine_grape g LEFT JOIN catalog_attribute_reference r ON r.kind = 'grape' AND r.code = g.grape WHERE g.grape IS NOT NULL",
                (rs, n) -> new MultiSelectFilterDto.Option(rs.getString(1), rs.getString(2) != null ? rs.getString(2) :
                    com.innovativesoftware.domsommelier_backend.product_management.product.util.RussianLabelTranslator.translateGrape(rs.getString(1))));
            if (!grapes.isEmpty()) {
                grapes.sort(Comparator.comparing(MultiSelectFilterDto.Option::getLabel, String.CASE_INSENSITIVE_ORDER));
                result.put("grape", MultiSelectFilterDto.builder().id(UUID.nameUUIDFromBytes("wine:grape".getBytes(StandardCharsets.UTF_8)))
                    .category(category).field("grape").name("Сорт винограда").type(FilterType.multi_select).selectionMode("value")
                    .options(grapes.toArray(MultiSelectFilterDto.Option[]::new)).build());
            }
        }
        for (var field : CatalogFilterFields.ALL) {
            if (!field.supports(category.name())) continue;
            result.remove(field.key());
            String source = field.key().equals("brand") ? "p.brand" : "t.extended_details";
            String path = CatalogAttributePredicates.path(field);
            String expression = source + " #>> '{" + path.replace('.', ',') + "}'";
            String from = from(category);
            FilterDto dto;
            if (field.range()) {
                var range = RangeFilterDto.builder().min(0d).max(0d).unit(field.unit()).steps(new RangeFilterDto.Step[0]).build();
                if (!setBounds(range, "(" + expression + ")::numeric", from)) continue;
                range.setType(FilterType.range); dto = range;
            } else {
                if (field.array()) {
                    from += " CROSS JOIN LATERAL jsonb_path_query(" + source + ", '$." + path + "') AS entry(value)";
                    expression = "entry.value #>> '{}'";
                }
                // SQL identifiers and paths come exclusively from the fixed registry above.
                var options = jdbc.query("SELECT DISTINCT ref.code, ref.label " + from
                    + " JOIN catalog_attribute_reference ref ON ref.kind = ? AND ref.code = (" + expression + ") ORDER BY ref.label",
                    (rs, n) -> new MultiSelectFilterDto.Option(rs.getString(1), rs.getString(2)), field.dictionary());
                if (options.isEmpty()) continue;
                dto = MultiSelectFilterDto.builder().type(FilterType.multi_select).options(options.toArray(MultiSelectFilterDto.Option[]::new)).build();
                dto.setSelectionMode("value");
            }
            dto.setId(UUID.nameUUIDFromBytes((category.name() + ":" + field.key()).getBytes(StandardCharsets.UTF_8)));
            dto.setCategory(category); dto.setField(field.key()); dto.setName(field.label()); dto.setSubtype(field.subtype());
            result.put(field.key(), dto);
        }
        return result;
    }
    private String from(ProductCategoryEnum category) {
        return "FROM " + TABLES.get(category.name()) + " t JOIN product p ON p.id = t.id";
    }
    private boolean setBounds(RangeFilterDto dto, String expression, String from) {
        var bounds = jdbc.queryForMap("SELECT min(" + expression + ") AS min, max(" + expression + ") AS max " + from);
        if (bounds.get("min") == null || bounds.get("max") == null) return false;
        dto.setMin(((Number) bounds.get("min")).doubleValue()); dto.setMax(((Number) bounds.get("max")).doubleValue());
        if (dto.getSteps() == null) dto.setSteps(new RangeFilterDto.Step[0]);
        return true;
    }
}
