package com.innovativesoftware.domsommelier_backend.filter_management.catalog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class CatalogAttributePredicates {
    private static final ObjectMapper JSON = new ObjectMapper();
    private CatalogAttributePredicates() {}
    public static Expression<?> source(CatalogFilterFields.Field field, Root<?> root) {
        return field.key().equals("brand") ? root.get("product").get("brand") : root.get("extendedDetails");
    }
    public static String path(CatalogFilterFields.Field field) {
        return field.key().equals("brand") ? "code" : field.path();
    }
    public static Expression<String> value(CatalogFilterFields.Field field, Root<?> root, CriteriaBuilder cb) {
        Expression<?> source = source(field, root);
        String path = path(field);
        if (field.array()) {
            // A set-returning expression: GROUP BY expands array values; countDistinct prevents duplicates.
            Expression<?> entries = cb.function("jsonb_path_query", Object.class, source,
                cb.function("jsonpath", Object.class, cb.literal("$." + path.substring(0, path.length() - 5))));
            return cb.function("jsonb_extract_path_text", String.class, entries, cb.literal("code"));
        }
        List<Expression<?>> args = new ArrayList<>(); args.add(source);
        for (String part : path.split("\\.")) args.add(cb.literal(part));
        return cb.function("jsonb_extract_path_text", String.class, args.toArray(Expression[]::new));
    }
    public static void add(String category, Map<String, Object> params, Root<?> root, CriteriaBuilder cb, List<Predicate> predicates) {
        for (var field : CatalogFilterFields.ALL) {
            Object input = params.get(field.key());
            if (!field.supports(category) || input == null || input instanceof List<?> l && l.isEmpty()) continue;
            if (!(input instanceof List<?> values)) throw bad(field.key());
            if (field.range()) {
                if (values.size() != 2) throw bad(field.key());
                BigDecimal min = number(values.get(0), field.key()), max = number(values.get(1), field.key());
                if (min != null && max != null && min.compareTo(max) > 0) throw bad(field.key());
                var number = value(field, root, cb).as(BigDecimal.class);
                if (min != null) predicates.add(cb.ge(number, min));
                if (max != null) predicates.add(cb.le(number, max));
            } else if (field.array()) {
                List<Predicate> alternatives = new ArrayList<>();
                for (Object item : values) {
                    if (!(item instanceof String code)) throw bad(field.key());
                    try {
                        String path = "$." + path(field) + " ? (@ == " + JSON.writeValueAsString(code) + ")";
                        alternatives.add(cb.isTrue(cb.function("jsonb_path_exists", Boolean.class, source(field, root),
                            cb.function("jsonpath", Object.class, cb.literal(path)))));
                    } catch (JsonProcessingException e) { throw bad(field.key()); }
                }
                predicates.add(cb.or(alternatives.toArray(Predicate[]::new)));
            } else {
                predicates.add(value(field, root, cb).in(values));
            }
        }
    }
    private static BigDecimal number(Object value, String field) {
        if (value == null) return null;
        try { return new BigDecimal(value.toString()); }
        catch (NumberFormatException e) { throw bad(field); }
    }
    private static ResponseStatusException bad(String field) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверное значение фильтра " + field);
    }
}
