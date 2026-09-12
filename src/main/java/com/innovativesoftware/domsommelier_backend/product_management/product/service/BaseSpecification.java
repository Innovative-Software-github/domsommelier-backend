package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.warehouse.entity.ProductStock;
import com.innovativesoftware.domsommelier_backend.filter_management.catalog.CatalogFilterFields;
import com.innovativesoftware.domsommelier_backend.filter_management.catalog.CatalogAttributePredicates;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public abstract class BaseSpecification<T> {

    /** Ключ параметра города (slug винотеки) в карте фильтров. */
    public static final String CITY_PARAM = "city";

    protected boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof List<?> list) return list.isEmpty();
        if (value instanceof String str) return str.trim().isEmpty();
        return false;
    }

    /**
     * Прогоняет список значений фильтра (пришедших от клиента как label,
     * см. MultiSelectFilter.tsx на фронтенде) через переводчик обратно к
     * тому, что реально хранится в колонке БД — нужно там, где отображаемая
     * подпись (после перевода на русский) отличается от исходного значения
     * в базе (grape/features, см. RussianLabelTranslator).
     */
    protected List<String> untranslate(List<?> values, UnaryOperator<String> untranslator) {
        return values.stream()
                .map(v -> untranslator.apply(String.valueOf(v)))
                .collect(Collectors.toList());
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
                case "price" -> addRangePredicates(value, root.get("product").<Number>get("price"), cb, predicates);
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
     * Range-фильтр приходит как [от, до], и любая граница может быть null («до 5000 ₽»).
     * Раньше обе границы прогонялись через toNumber, и фильтр с одной границей падал 400.
     */
    protected void addRangePredicates(Object value, Expression<? extends Number> field,
                                      CriteriaBuilder cb, List<Predicate> predicates) {
        if (!(value instanceof List<?> range) || range.size() != 2) {
            return;
        }
        if (range.get(0) != null) {
            predicates.add(cb.ge(field, toNumber(range.get(0))));
        }
        if (range.get(1) != null) {
            predicates.add(cb.le(field, toNumber(range.get(1))));
        }
    }

    /**
     * Значение multi_select-фильтра, по которому {@link FacetCounter} считает, сколько
     * товаров даст каждый вариант. null — поле фасетами не считается.
     */
    public Expression<?> facetValue(String field, Root<T> root, CriteriaBuilder cb) {
        String category = CatalogFilterFields.category(root.getJavaType());
        var extended = CatalogFilterFields.ALL.stream().filter(f -> f.key().equals(field) && f.supports(category) && !f.range()).findFirst();
        if (extended.isPresent()) return CatalogAttributePredicates.value(extended.get(), root, cb);
        return switch (field) {
            case "countries" -> root.get("product").get("productCountry").get("name");
            case "producer" -> root.get("producer");
            default -> specificFacetValue(field, root);
        };
    }

    protected abstract Expression<?> specificFacetValue(String field, Root<T> root);

    /**
     * Значение из БД → подпись варианта в том виде, в каком её отдаёт FilterFieldProvider
     * и присылает фронт. Переопределяется там, где в БД лежат коды (особенности, сорта).
     */
    public String facetLabel(String field, String rawValue) {
        return rawValue;
    }

    /**
     * Ограничивает выборку товарами, доступными в указанном городе:
     * есть остаток ({@code quantity > 0}) хотя бы в одной винотеке этого города.
     * <p>
     * Реализовано через {@code EXISTS}-подзапрос, а не join + {@code DISTINCT}:
     * при {@code DISTINCT} PostgreSQL требует, чтобы все выражения {@code ORDER BY}
     * (сортировка витрины по полям {@code product.*}) присутствовали в списке выборки,
     * что несовместимо с {@code DISTINCT} по колонкам подтипа. {@code EXISTS} убирает
     * дубликаты без {@code DISTINCT}. Если город не задан — предикат не добавляется.
     */
    private void addCityAvailabilityPredicate(Map<String, Object> params, Root<T> root, CriteriaQuery<?> query,
                                              CriteriaBuilder cb, List<Predicate> predicates) {
        Object city = params.get(CITY_PARAM);
        if (isEmpty(city) || query == null) return;

        Subquery<Integer> stockExists = query.subquery(Integer.class);
        Root<ProductStock> stock = stockExists.from(ProductStock.class);
        Join<?, ?> store = stock.join("wineStore");
        stockExists.select(cb.literal(1));
        stockExists.where(
                cb.equal(stock.get("product"), root.get("product")),
                cb.equal(store.get("city"), city.toString()),
                cb.gt(stock.get("quantity"), 0)
        );
        predicates.add(cb.exists(stockExists));
    }

    public Specification<T> byFilter(Map<String, Object> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            var normalized = CatalogFilterFields.normalize(params);
            addCommonPredicates(normalized, root, cb, predicates);
            addSpecificPredicates(normalized, root, cb, predicates);
            CatalogAttributePredicates.add(CatalogFilterFields.category(root.getJavaType()), normalized, root, cb, predicates);
            addCityAvailabilityPredicate(params, root, query, cb, predicates);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
