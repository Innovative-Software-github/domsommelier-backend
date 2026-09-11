package com.innovativesoftware.domsommelier_backend.product_management.product;

import com.innovativesoftware.domsommelier_backend.product_management.product.service.SearchQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchQueryTest {

    @ParameterizedTest(name = "«{0}» → «{1}»")
    @CsvSource({
            // Слово ищется подстрокой, поэтому основа должна входить в значение справочника.
            "красное, красн",       // wine_color «Красное»
            "красного, красн",
            "белое, бело",           // не «бел» — иначе нашлась бы «Бельгия»
            "сухое, сухо",           // и «Полусухое»
            "вина, вин",             // категория «Вино»
            "вино, вин",
            "виски, виск",           // spirit_category «Виски»
            "водки, водк",
            "шампанское, шампанск",
            "игристые, игрист",
            "франции, франци",       // страна «Франция»
            "коньяк, коньяк",
            "ром, ром",              // короткие слова не трогаем
            "moet, moet",            // латиницу не трогаем
    })
    void stemsRussianEndings(String word, String expectedStem) {
        assertThat(SearchQuery.parse(word).tokenVariants()).containsExactly(List.of(expectedStem));
    }

    @Test
    void splitsIntoWordsDropsStopWordsAndDuplicates() {
        SearchQuery query = SearchQuery.parse("  Красное   вино для сыра, красное ");

        assertThat(query.tokenVariants())
                .containsExactly(List.of("красн"), List.of("вин"), List.of("сыр"));
        assertThat(query.phrase()).isEqualTo("красное вино для сыра, красное");
    }

    @Test
    void keepsStopWordWhenItIsTheWholeQuery() {
        assertThat(SearchQuery.parse("в").tokenVariants()).containsExactly(List.of("в"));
    }

    @Test
    void addsEnglishSynonymForLowAlcoholCategories() {
        assertThat(SearchQuery.parse("сидры").tokenVariants())
                .containsExactly(List.of("сидр", "cider"));
    }

    @Test
    void limitsNumberOfWords() {
        SearchQuery query = SearchQuery.parse("a b c d e f g h");

        assertThat(query.tokenVariants()).hasSize(6);
    }

    @Test
    void emptyAndBlankQueriesAreEmpty() {
        assertThat(SearchQuery.parse(null).isEmpty()).isTrue();
        assertThat(SearchQuery.parse("   ").isEmpty()).isTrue();
    }
}
