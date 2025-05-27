package com.innovativesoftware.domsommelier_backend.product_management.product.service;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.wine.Wine;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateQuery {

    private final EntityManager entityManager;

    public CriteriaQuery<UUID> createQuery(ProductCategoryEnum category, Map<String, Object> params) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UUID> query = cb.createQuery(UUID.class);
        Root<Product> product = query.from(Product.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(product.get("productCategory").get("name"), category));

        // Универсальные фильтры для всех категорий
        if (params.containsKey("price")) {
            List<Integer> priceRange = (List<Integer>) params.get("price");
            if (priceRange.size() == 2) {
                predicates.add(cb.ge(product.get("price"), priceRange.get(0)));
                predicates.add(cb.le(product.get("price"), priceRange.get(1)));
            }
        }

        if (params.containsKey("countries")) {
            List<String> countries = (List<String>) params.get("countries");
            if (!countries.isEmpty())
                predicates.add(product.get("productCountry").get("name").as(String.class).in(countries));
        }

        switch (category) {
            case WINE:
                predicates = addWinePredicates(query, product, cb, predicates, params);
                break;
            case SPIRIT:
                predicates = addSpiritsPredicates(query, product, cb, predicates, params);
                break;
            case CHAMPAGNE_AND_SPARKLING:
                predicates = addShampagnePredicates(query, product, cb, predicates, params);
                break;
            case LOW_ALCOHOL:
                predicates = addLowAlcoholPredicates(query, product, cb, predicates, params);
            case SNACK:
                predicates = addSnackPredicates(query, product, cb, predicates, params);
                break;
            case ACCESSORIES:
                predicates = addAccessoriesPredicates(query, product, cb, predicates, params);
                break;
        }

        query.select(product.get("id")).where(cb.and(predicates.toArray(new Predicate[0])));
        return query;
    }

    private List<Predicate> addWinePredicates(CriteriaQuery<UUID> query, Root<Product> product, CriteriaBuilder cb,
                                              List<Predicate> predicates, Map<String, Object> params) {
        Root<Wine> wine = query.from(Wine.class);
        predicates.add(cb.equal(product.get("id"), wine.get("id")));

        if (params.containsKey("type")) {
            List<String> type = (List<String>) params.get("type");
            if (!type.isEmpty())
                predicates.add(wine.get("type").get("name").as(String.class).in(type));
        }
        if (params.containsKey("color")) {
            List<String> colors = (List<String>) params.get("color");
            if (!colors.isEmpty())
                predicates.add(wine.get("color").get("name").as(String.class).in(colors));
        }

        return predicates;
    }

    private List<Predicate> addSpiritsPredicates(CriteriaQuery<UUID> query, Root<Product> product, CriteriaBuilder cb,
                                                 List<Predicate> predicates, Map<String, Object> params) {
        /*
        Join<Product, Spirit> spiritJoin = product.join("spirit", JoinType.LEFT);

        if (params.containsKey("categorySpirit")) {
            List<String> cats = (List<String>) params.get("categorySpirit");
            if (!cats.isEmpty())
                predicates.add(spiritJoin.get("category").in(cats));
        }
        if (params.containsKey("strength")) {
            List<String> strengths = (List<String>) params.get("strength");
            if (!strengths.isEmpty())
                predicates.add(spiritJoin.get("strength").in(strengths));
        }*/
        return predicates;
    }

    private List<Predicate> addShampagnePredicates(CriteriaQuery<UUID> query, Root<Product> product, CriteriaBuilder cb,
                                                   List<Predicate> predicates, Map<String, Object> params) {
        /*
        Join<Product, Champagne> champJoin = product.join("champagne", JoinType.LEFT);

        if (params.containsKey("categoryChampagne")) {
            List<String> chCats = (List<String>) params.get("categoryChampagne");
            if (!chCats.isEmpty())
                predicates.add(champJoin.get("category").in(chCats));
        }
        if (params.containsKey("sugar")) {
            List<String> sugar = (List<String>) params.get("sugar");
            if (!sugar.isEmpty())
                predicates.add(champJoin.get("sugar").in(sugar));
        }
        if (params.containsKey("color")) {
            List<String> colors = (List<String>) params.get("color");
            if (!colors.isEmpty())
                predicates.add(champJoin.get("color").in(colors));
        }*/
        return predicates;
    }

    private List<Predicate> addLowAlcoholPredicates(CriteriaQuery<UUID> query, Root<Product> product, CriteriaBuilder cb,
                                                    List<Predicate> predicates, Map<String, Object> params) {
        /*Join<Product, LowAlcohol> lowJoin = product.join("lowAlcohol", JoinType.LEFT);

        if (params.containsKey("categoryLow")) {
            List<String> cats = (List<String>) params.get("categoryLow");
            if (!cats.isEmpty())
                predicates.add(lowJoin.get("category").in(cats));
        }
        if (params.containsKey("strength")) {
            List<String> strengths = (List<String>) params.get("strength");
            if (!strengths.isEmpty())
                predicates.add(lowJoin.get("strength").in(strengths));
        }*/
        return predicates;
    }

    private List<Predicate> addSnackPredicates(CriteriaQuery<UUID> query, Root<Product> product, CriteriaBuilder cb,
                                               List<Predicate> predicates, Map<String, Object> params) {
        /*Join<Product, Snack> snackJoin = product.join("snack", JoinType.LEFT);

        if (params.containsKey("categorySnack")) {
            List<String> cats = (List<String>) params.get("categorySnack");
            if (!cats.isEmpty())
                predicates.add(snackJoin.get("category").in(cats));
        }
        if (params.containsKey("drinkPairing")) {
            List<String> drinkPairing = (List<String>) params.get("drinkPairing");
            if (!drinkPairing.isEmpty())
                predicates.add(snackJoin.get("drinkPairing").in(drinkPairing));
        }*/
        return predicates;
    }

    private List<Predicate> addAccessoriesPredicates(CriteriaQuery<UUID> query, Root<Product> product, CriteriaBuilder cb,
                                                     List<Predicate> predicates, Map<String, Object> params) {
        return predicates;
    }
}
