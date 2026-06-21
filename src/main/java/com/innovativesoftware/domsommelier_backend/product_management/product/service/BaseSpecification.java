package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseSpecification<T> {

    /** Ключ параметра города (slug винотеки) в карте фильтров. */
    public static final String CITY_PARAM = "city";

    protected boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof List<?> list) return list.isEmpty();
        if (value instanceof String str) return str.trim().isEmpty();
        return false;
    }

    protected Number toNumber(Object obj) {
        if (obj instanceof Number) return (Number) obj;
        if (obj instanceof String s) return Double.parseDouble(s);
        throw new IllegalArgumentException("Cannot convert to number: " + obj);
    }

    protected void addCommonPredicates(Map<String, Object> params, Root<T> root, CriteriaBuilder cb, List<Predicate> predicates) {
        params.forEach((key, value) -> {
            if (isEmpty(value)) return;
            switch (key) {
                case "price" -> {
                    if (value instanceof List<?> priceRange && priceRange.size() == 2) {
                        predicates.add(cb.ge(root.get("product").get("price"), toNumber(priceRange.get(0))));
                        predicates.add(cb.le(root.get("product").get("price"), toNumber(priceRange.get(1))));
                    }
                }
                case "countries" -> predicates.add(root.get("product").get("productCountry").get("name").in((List<?>) value));
                case "producer" -> {
                    if (value instanceof List<?> l) {
                        predicates.add(root.get("producer").in(l));
                    } else {
                        predicates.add(cb.equal(root.get("producer"), value));
                    }
                }
                case "description" -> predicates.add(cb.like(cb.lower(root.get("product").get("description")),
                        "%" + value.toString().toLowerCase() + "%"));
            }
        });
    }

    protected abstract void addSpecificPredicates(Map<String, Object> params, Root<T> root, CriteriaBuilder cb, List<Predicate> predicates);

    /**
     * Ограничивает выборку товарами, доступными в указанном городе:
     * есть остаток ({@code quantity > 0}) хотя бы в одной винотеке этого города.
     * <p>
     * Применяется ко всем категориям, т.к. корень спецификации — подтип товара,
     * связанный с {@code Product} через {@code product}. Если город не задан —
     * предикат не добавляется и поведение остаётся прежним.
     */
    private void addCityAvailabilityPredicate(Map<String, Object> params, Root<T> root, CriteriaQuery<?> query,
                                              CriteriaBuilder cb, List<Predicate> predicates) {
        Object city = params.get(CITY_PARAM);
        if (isEmpty(city)) return;

        Join<?, ?> stock = root.join("product").join("stocks");
        Join<?, ?> store = stock.join("wineStore");

        predicates.add(cb.equal(store.get("city"), city.toString()));
        predicates.add(cb.gt(stock.get("quantity"), 0));

        // join к коллекции остатков может дублировать строки товара — убираем дубликаты
        if (query != null) {
            query.distinct(true);
        }
    }

    public Specification<T> byFilter(Map<String, Object> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addCommonPredicates(params, root, cb, predicates);
            addSpecificPredicates(params, root, cb, predicates);
            addCityAvailabilityPredicate(params, root, query, cb, predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
