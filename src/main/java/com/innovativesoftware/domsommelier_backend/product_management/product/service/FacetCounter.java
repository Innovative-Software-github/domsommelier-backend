package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductFacetsDto;
import com.innovativesoftware.domsommelier_backend.filter_management.catalog.CatalogFilterFields;
import java.util.LinkedHashSet;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Считает фасеты каталога: сколько товаров даст каждый вариант multi_select-фильтра.
 * <p>
 * Вариант поля считается с учётом всех остальных выбранных фильтров, но не этого же
 * поля — иначе, отметив «Виски», пользователь увидел бы у «Коньяка» ноль и не смог бы
 * добавить его к выбору. Фильтрация — те же {@link BaseSpecification}, что и у выдачи,
 * поэтому счётчики не разойдутся с результатом.
 */
@Component
@RequiredArgsConstructor
public class FacetCounter {

    private final EntityManager entityManager;

    public <T> ProductFacetsDto count(Class<T> entityClass, BaseSpecification<T> specification,
                                      Map<String, Object> params, Collection<String> fields) {
        Map<String, Map<String, Long>> options = new LinkedHashMap<>();
        var allFields = new LinkedHashSet<>(fields);
        CatalogFilterFields.ALL.stream().filter(f -> f.supports(CatalogFilterFields.category(entityClass)) && !f.range()
                && (f.subtype() == null || f.subtype().equals(CatalogFilterFields.subtype(params.get("subcategory")))))
            .forEach(f -> allFields.add(f.key()));
        for (String field : allFields) {
            Map<String, Object> otherFilters = new HashMap<>(params);
            otherFilters.remove(field);
            if (field.equals("subcategory")) CatalogFilterFields.ALL.stream().filter(f -> f.subtype() != null)
                .forEach(f -> otherFilters.remove(f.key()));

            Map<String, Long> counts = countField(entityClass, specification, otherFilters, field);
            if (counts != null) {
                options.put(field, counts);
            }
        }
        return new ProductFacetsDto(countTotal(entityClass, specification, params), options);
    }

    private <T> Map<String, Long> countField(Class<T> entityClass, BaseSpecification<T> specification,
                                             Map<String, Object> params, String field) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<T> root = query.from(entityClass);

        Expression<?> value = specification.facetValue(field, root, cb);
        if (value == null) {
            return null;
        }
        // Строкой, как подписи вариантов в FilterFieldProvider (cast(... as string)):
        // объём 0.70 должен совпасть с подписью «0.70».
        Expression<String> label = value.as(String.class);

        query.multiselect(label, cb.countDistinct(root))
                .where(specification.byFilter(params).toPredicate(root, query, cb))
                .groupBy(label);

        Map<String, Long> counts = new LinkedHashMap<>();
        for (Tuple row : entityManager.createQuery(query).getResultList()) {
            String raw = row.get(0, String.class);
            if (raw != null) {
                String translated = specification.facetLabel(field, raw);
                counts.merge(translated, row.get(1, Long.class), Long::sum);
                // Wine grape now uses dictionary codes; keep label keys for existing clients too.
                if (field.equals("grape") && !raw.equals(translated)) counts.merge(raw, row.get(1, Long.class), Long::sum);
            }
        }
        return counts;
    }

    private <T> long countTotal(Class<T> entityClass, BaseSpecification<T> specification, Map<String, Object> params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(entityClass);

        query.select(cb.countDistinct(root))
                .where(specification.byFilter(params).toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }
}
