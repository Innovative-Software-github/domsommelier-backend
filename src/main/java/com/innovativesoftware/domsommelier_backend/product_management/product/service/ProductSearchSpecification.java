package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.accessories.Accessories;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.champaigne_sparkling.SparklingWine;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.low_alcohol.LowAlcohol;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.snack.Snack;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.spirit.Spirit;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.ProductStock;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProductSearchSpecification {

    private static final char LIKE_ESCAPE = '\\';

    private ProductSearchSpecification() {
    }

    /**
     * Каждое слово запроса должно найтись хотя бы в одном поле товара: название, артикул,
     * страна, категория, производитель и справочники подтипа (цвет и тип вина, вид крепкого,
     * категория/цвет/сахар игристого, категория закусок и слабоалкогольных). Раньше искали
     * только по названию и артикулу, а названия в каталоге — латинские бренды, поэтому
     * «виски», «красное вино» или «Франция» ничего не находили.
     * <p>
     * Город — через {@code EXISTS}, как в каталоге ({@link BaseSpecification}), а не
     * join + {@code DISTINCT}: с {@code DISTINCT} PostgreSQL не даёт сортировать по
     * выражению релевантности.
     */
    public static Specification<Product> byQueryAndCity(SearchQuery searchQuery, String city) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (List<String> variants : searchQuery.tokenVariants()) {
                List<Predicate> anyVariant = new ArrayList<>();
                for (String variant : variants) {
                    anyVariant.add(matchesAnyField(likePattern(variant), root, query, cb));
                }
                predicates.add(cb.or(anyVariant.toArray(new Predicate[0])));
            }

            if (city != null && query != null) {
                predicates.add(cb.exists(availableInCity(city, root, query, cb)));
            }

            // Count-запрос Spring Data (результат Long) сортировать незачем.
            if (query != null && !Long.class.equals(query.getResultType())) {
                query.orderBy(
                        cb.asc(relevance(searchQuery.phrase(), root, cb)),
                        cb.asc(cb.lower(root.<String>get("name"))),
                        cb.asc(root.get("id")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate matchesAnyField(String pattern, Root<Product> product,
                                             CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> fields = new ArrayList<>(List.of(
                like(cb, product.<String>get("name"), pattern),
                like(cb, product.<String>get("article"), pattern),
                like(cb, product.get("productCountry").<String>get("name"), pattern),
                like(cb, product.get("productCategory").<String>get("label"), pattern)));

        if (query != null) {
            fields.add(cb.exists(subtypeMatches(Wine.class, pattern, product, query, cb, "color", "type")));
            fields.add(cb.exists(subtypeMatches(Spirit.class, pattern, product, query, cb, "category")));
            fields.add(cb.exists(subtypeMatches(SparklingWine.class, pattern, product, query, cb,
                    "subcategory", "color", "sugarContent")));
            fields.add(cb.exists(subtypeMatches(LowAlcohol.class, pattern, product, query, cb, "category")));
            fields.add(cb.exists(subtypeMatches(Snack.class, pattern, product, query, cb, "subcategory")));
            fields.add(cb.exists(subtypeMatches(Accessories.class, pattern, product, query, cb)));
        }

        return cb.or(fields.toArray(new Predicate[0]));
    }

    /** Подтип товара с этим id, у которого производитель или одно из справочных полей подходит под шаблон. */
    private static Subquery<Integer> subtypeMatches(Class<?> subtype, String pattern, Root<Product> product,
                                                    CriteriaQuery<?> query, CriteriaBuilder cb,
                                                    String... dictionaries) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<?> sub = subquery.from(subtype);

        List<Predicate> fields = new ArrayList<>();
        fields.add(like(cb, sub.<String>get("producer"), pattern));
        for (String dictionary : dictionaries) {
            fields.add(like(cb, sub.join(dictionary, JoinType.LEFT).<String>get("name"), pattern));
        }

        subquery.select(cb.literal(1)).where(
                cb.equal(sub.get("product"), product),
                cb.or(fields.toArray(new Predicate[0])));
        return subquery;
    }

    private static Subquery<Integer> availableInCity(String city, Root<Product> product,
                                                     CriteriaQuery<?> query, CriteriaBuilder cb) {
        Subquery<Integer> subquery = query.subquery(Integer.class);
        Root<ProductStock> stock = subquery.from(ProductStock.class);
        Join<?, ?> store = stock.join("wineStore");

        subquery.select(cb.literal(1)).where(
                cb.equal(stock.get("product"), product),
                cb.equal(store.get("city"), city),
                cb.gt(stock.get("quantity"), 0));
        return subquery;
    }

    /** 0 — точное совпадение артикула, 1 — название начинается с запроса, 2 — содержит его, 3 — остальное. */
    private static Expression<Integer> relevance(String phrase, Root<Product> product, CriteriaBuilder cb) {
        String escaped = escapeLike(phrase);
        Expression<String> name = cb.lower(product.<String>get("name"));

        return cb.<Integer>selectCase()
                .when(cb.equal(cb.lower(product.<String>get("article")), phrase), 0)
                .when(cb.like(name, escaped + "%", LIKE_ESCAPE), 1)
                .when(cb.like(name, "%" + escaped + "%", LIKE_ESCAPE), 2)
                .otherwise(3);
    }

    private static Predicate like(CriteriaBuilder cb, Expression<String> field, String pattern) {
        return cb.like(cb.lower(field), pattern, LIKE_ESCAPE);
    }

    /** % и _ из запроса — обычные символы, а не шаблон LIKE. */
    private static String likePattern(String term) {
        return "%" + escapeLike(term) + "%";
    }

    private static String escapeLike(String term) {
        return term.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
