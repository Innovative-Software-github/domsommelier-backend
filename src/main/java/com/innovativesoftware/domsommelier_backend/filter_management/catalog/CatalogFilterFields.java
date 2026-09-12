package com.innovativesoftware.domsommelier_backend.filter_management.catalog;

import java.util.*;

/** One allowlist for SQL paths, API fields, ranges and subtype dependencies. */
public final class CatalogFilterFields {
    private CatalogFilterFields() {}
    public record Field(String key, String label, Set<String> categories, String path,
                        String dictionary, boolean array, boolean range, String unit, String subtype) {
        public boolean supports(String category) { return categories.contains(category); }
    }
    private static final Set<String> BEVERAGES = Set.of("wine", "champagne_and_sparkling", "spirit");
    private static final Set<String> WINES = Set.of("wine", "champagne_and_sparkling");
    private static final Set<String> SPIRIT = Set.of("spirit");
    public static final List<Field> ALL = List.of(
        new Field("brand", "Бренд", Set.of("wine", "champagne_and_sparkling", "spirit", "low_alcohol", "snack", "accessories"), "brand.code", "brand", false, false, null, null),
        new Field("region", "Регион", BEVERAGES, "region.code", "region", false, false, null, null),
        new Field("appellation", "Аппелласьон", BEVERAGES, "appellation.code", "appellation", false, false, null, null),
        new Field("strength", "Крепость", WINES, "strength", null, false, true, "%", null),
        new Field("grapeComposition", "Сорт винограда", Set.of("champagne_and_sparkling"), "grapeComposition[*].grape.code", "grape", true, false, null, null),
        new Field("year", "Год урожая", Set.of("champagne_and_sparkling"), "productionYear", null, false, true, "", null),
        new Field("sparklingMethod", "Метод производства", Set.of("champagne_and_sparkling"), "sparklingMethod.code", "sparkling_method", false, false, null, null),
        new Field("agingVessel", "Ёмкость выдержки", BEVERAGES, "aging.vessels[*].code", "vessel", true, false, null, null),
        new Field("whiskyType", "Тип виски", SPIRIT, "whiskyDetails.whiskyType.code", "whisky_type", false, false, null, "whisky"),
        new Field("whiskyBlendStyle", "Стиль купажа", SPIRIT, "whiskyDetails.blendStyle.code", "blend_style", false, false, null, "whisky"),
        new Field("whiskyAge", "Заявленный возраст виски", SPIRIT, "whiskyDetails.ageStatementYears", null, false, true, "лет", "whisky"),
        new Field("cognacClassification", "Классификация коньяка", SPIRIT, "cognacDetails.ageClassification.code", "age_classification", false, false, null, "cognac"),
        new Field("cognacAge", "Заявленный возраст коньяка", SPIRIT, "cognacDetails.ageStatementYears", null, false, true, "лет", "cognac"),
        new Field("cognacOrigin", "Зона происхождения коньяка", SPIRIT, "cognacDetails.originArea.code", "origin_area", false, false, null, "cognac")
    );
    public static String category(Class<?> entityClass) {
        return switch (entityClass.getSimpleName()) {
            case "Wine" -> "wine";
            case "SparklingWine" -> "champagne_and_sparkling";
            case "Spirit" -> "spirit";
            case "LowAlcohol" -> "low_alcohol";
            case "Snack" -> "snack";
            case "Accessories" -> "accessories";
            default -> "";
        };
    }
    public static String subtype(Object selection) {
        if (!(selection instanceof List<?> values) || values.size() != 1) return null;
        return switch (String.valueOf(values.get(0)).trim().toLowerCase(Locale.ROOT)) {
            case "виски", "whisky", "whiskey" -> "whisky";
            case "коньяк", "cognac" -> "cognac";
            default -> null;
        };
    }
    public static Map<String, Object> normalize(Map<String, Object> params) {
        var result = new HashMap<>(params);
        String subtype = subtype(params.get("subcategory"));
        ALL.stream().filter(f -> f.subtype() != null && !f.subtype().equals(subtype)).forEach(f -> result.remove(f.key()));
        return result;
    }
}
