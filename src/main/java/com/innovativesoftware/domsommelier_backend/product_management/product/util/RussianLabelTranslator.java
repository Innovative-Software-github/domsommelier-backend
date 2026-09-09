package com.innovativesoftware.domsommelier_backend.product_management.product.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Переводит "сырые" английские слаги, которые хранятся в БД для сорта
 * винограда и особенностей товара (wine_grape/wine_feature/spirit_feature —
 * @ElementCollection<String>, без справочника-сущности с именем, в отличие
 * от цвета/типа/страны), в человекочитаемые русские подписи.
 *
 * Значение, для которого нет перевода, возвращается как есть — новый слаг
 * из БД не пропадёт и не сломается, просто останется непереведённым до
 * добавления сюда записи.
 */
public final class RussianLabelTranslator {

    private RussianLabelTranslator() {}

    private static final Map<String, String> GRAPE_NAMES = Map.ofEntries(
            Map.entry("cabernet_sauvignon", "Каберне Совиньон"),
            Map.entry("carignan", "Кариньян"),
            Map.entry("chardonnay", "Шардоне"),
            Map.entry("grenache", "Гренаш"),
            Map.entry("merlot", "Мерло"),
            Map.entry("pinot_noir", "Пино Нуар"),
            Map.entry("sangiovese", "Санджовезе"),
            Map.entry("sauvignon_blanc", "Совиньон Блан"),
            Map.entry("solaris", "Солярис"),
            Map.entry("tempranillo", "Темпранильо"),
            Map.entry("tsimlyansky_black", "Цимлянский чёрный")
    );

    /** Общий словарь для wine_feature и spirit_feature — коды не пересекаются. */
    private static final Map<String, String> FEATURE_NAMES = Map.ofEntries(
            Map.entry("gift_wrapping", "Подарочная упаковка"),
            Map.entry("collection", "Коллекционное"),
            Map.entry("gift_set", "Подарочный набор"),
            Map.entry("collectible", "Коллекционный"),
            Map.entry("limited_edition", "Лимитированная серия"),
            Map.entry("award_winning", "Премированное")
    );

    private static final Map<String, String> GRAPE_CODES_BY_NAME = reverse(GRAPE_NAMES);
    private static final Map<String, String> FEATURE_CODES_BY_NAME = reverse(FEATURE_NAMES);

    public static String translateGrape(String raw) {
        return translate(raw, GRAPE_NAMES);
    }

    public static String translateFeature(String raw) {
        return translate(raw, FEATURE_NAMES);
    }

    /**
     * Обратный перевод: фронтенд отправляет выбранный фильтр по label (так
     * устроены все multi_select-фильтры в проекте — см. MultiSelectFilter.tsx,
     * матчит по label, не по value), а в БД до сих пор лежит исходный
     * английский код (wine_grape.grape/wine_feature.feature/spirit_feature.feature
     * не мигрировали на русский текст, в отличие от цвета/типа). Без этого
     * перевода назад сам фильтр перестал бы находить товары после того как
     * подписи стали русскими.
     */
    public static String untranslateGrape(String label) {
        return translate(label, GRAPE_CODES_BY_NAME);
    }

    public static String untranslateFeature(String label) {
        return translate(label, FEATURE_CODES_BY_NAME);
    }

    public static List<String> untranslateGrapes(List<String> labels) {
        return translateAll(labels, GRAPE_CODES_BY_NAME);
    }

    public static List<String> untranslateFeatures(List<String> labels) {
        return translateAll(labels, FEATURE_CODES_BY_NAME);
    }

    private static Map<String, String> reverse(Map<String, String> dictionary) {
        return dictionary.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    public static List<String> translateGrapes(List<String> raw) {
        return translateAll(raw, GRAPE_NAMES);
    }

    public static List<String> translateFeatures(List<String> raw) {
        return translateAll(raw, FEATURE_NAMES);
    }

    public static Set<String> translateGrapes(Set<String> raw) {
        return translateAll(raw, GRAPE_NAMES);
    }

    public static Set<String> translateFeatures(Set<String> raw) {
        return translateAll(raw, FEATURE_NAMES);
    }

    private static String translate(String raw, Map<String, String> dictionary) {
        if (raw == null) return null;
        return dictionary.getOrDefault(raw, raw);
    }

    private static List<String> translateAll(List<String> raw, Map<String, String> dictionary) {
        if (raw == null) return null;
        return raw.stream().map(v -> translate(v, dictionary)).collect(Collectors.toList());
    }

    private static Set<String> translateAll(Set<String> raw, Map<String, String> dictionary) {
        if (raw == null) return null;
        return raw.stream().map(v -> translate(v, dictionary)).collect(Collectors.toSet());
    }
}
