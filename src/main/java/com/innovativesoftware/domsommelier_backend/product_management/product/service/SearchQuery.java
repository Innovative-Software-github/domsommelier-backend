package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Разбор поискового запроса витрины для {@link ProductSearchSpecification}.
 * <p>
 * Запрос режется на слова, каждое слово ищется подстрокой. Для кириллицы отрезается
 * окончание («красного вина» → «красн», «вин»), иначе «вина» не находило «Вино»,
 * а «водки» — «Водка». Это не настоящий стеммер, а дешёвое приближение, которого
 * хватает для справочников каталога (цвета, категории, страны).
 */
public final class SearchQuery {

    /** Больше слов не учитываем: каждое слово — несколько подзапросов в SQL. */
    static final int MAX_TOKENS = 6;

    private static final Set<String> STOP_WORDS = Set.of(
            "и", "в", "во", "на", "с", "со", "для", "из", "по", "к", "от", "до", "без");

    /** Длинные окончания раньше коротких: иначе от «красного» отрежется только «о». */
    private static final List<String> MULTI_LETTER_ENDINGS = List.of(
            "ого", "его", "ому", "ему", "ыми", "ими",
            "ая", "яя", "ое", "ее", "ую", "юю", "ый", "ий", "ой", "ей",
            "ые", "ие", "ых", "их", "ым", "им", "ом", "ем", "ам", "ям", "ах", "ях", "ов", "ев");

    private static final String SINGLE_LETTER_ENDINGS = "аяоеыиуюь";

    /**
     * Категории слабоалкогольных напитков хранятся кодами (cider, radler…), а названия
     * этих товаров — на английском, поэтому русское слово ищем ещё и по-английски.
     */
    private static final Map<String, String> SYNONYMS = Map.of(
            "сидр", "cider",
            "лимонад", "lemonade",
            "радлер", "radler",
            "сельтцер", "seltzer");

    private final String phrase;
    private final List<List<String>> tokenVariants;

    private SearchQuery(String phrase, List<List<String>> tokenVariants) {
        this.phrase = phrase;
        this.tokenVariants = tokenVariants;
    }

    public static SearchQuery parse(String raw) {
        String phrase = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (phrase.isEmpty()) {
            return new SearchQuery("", List.of());
        }

        List<String> words = Arrays.stream(phrase.split("[\\s,;]+"))
                .filter(word -> !word.isEmpty())
                .toList();
        List<String> meaningful = words.stream().filter(word -> !STOP_WORDS.contains(word)).toList();
        // Запрос из одних служебных слов («в») ищем как есть, а не отбрасываем.
        List<String> tokens = (meaningful.isEmpty() ? words : meaningful).stream()
                .distinct()
                .limit(MAX_TOKENS)
                .toList();

        List<List<String>> variants = new ArrayList<>();
        for (String token : tokens) {
            variants.add(variantsOf(token));
        }
        return new SearchQuery(phrase, List.copyOf(variants));
    }

    /** Весь запрос в нижнем регистре — для сортировки по релевантности. */
    public String phrase() {
        return phrase;
    }

    /** Для каждого слова — варианты написания; слово найдено, если найден любой из них. */
    public List<List<String>> tokenVariants() {
        return tokenVariants;
    }

    public boolean isEmpty() {
        return tokenVariants.isEmpty();
    }

    private static List<String> variantsOf(String token) {
        String stem = stem(token);
        List<String> variants = new ArrayList<>(List.of(stem));
        SYNONYMS.forEach((word, synonym) -> {
            if (stem.startsWith(word)) {
                variants.add(synonym);
            }
        });
        return List.copyOf(variants);
    }

    static String stem(String token) {
        if (token.length() < 4 || !isCyrillic(token)) {
            return token;
        }
        for (String ending : MULTI_LETTER_ENDINGS) {
            if (token.endsWith(ending) && token.length() - ending.length() >= 4) {
                return token.substring(0, token.length() - ending.length());
            }
        }
        char last = token.charAt(token.length() - 1);
        if (SINGLE_LETTER_ENDINGS.indexOf(last) >= 0) {
            return token.substring(0, token.length() - 1);
        }
        return token;
    }

    private static boolean isCyrillic(String token) {
        return token.chars().allMatch(ch -> ch == '-' || Character.UnicodeScript.of(ch) == Character.UnicodeScript.CYRILLIC);
    }
}
