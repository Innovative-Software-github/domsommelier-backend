package com.innovativesoftware.domsommelier_backend.product_management.store.service;

import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import com.innovativesoftware.domsommelier_backend.product_management.store.model.WineStoreFilterDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class WineStoreSpecification {

    public static Specification<WineStore> withFilter(WineStoreFilterDto filter) {
        return Specification.where(hasCity(filter.getCity()))
                .and(hasDistrict(filter.getDistrict()))
                .and(hasCoordinatesInBounds(filter));
    }

    private static Specification<WineStore> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                city == null || city.trim().isEmpty() ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("city"), city);
    }

    private static Specification<WineStore> hasDistrict(String district) {
        return (root, query, criteriaBuilder) ->
                district == null || district.trim().isEmpty() ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("district"), district);
    }

    private static Specification<WineStore> hasCoordinatesInBounds(WineStoreFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            Double minLon = filter.getMinLongitude();
            Double maxLon = filter.getMaxLongitude();
            Double minLat = filter.getMinLatitude();
            Double maxLat = filter.getMaxLatitude();

            if (minLon == null && maxLon == null && minLat == null && maxLat == null) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();



//            Expression<Double> longitude = criteriaBuilder.function(
//                    "ST_X", Double.class, root.get("location")
//            );
//            Expression<Double> latitude = criteriaBuilder.function(
//                    "ST_Y", Double.class, root.get("location")
//            );

//            if (minLon != null) {
//                predicates.add(criteriaBuilder.greaterThanOrEqualTo(longitude, minLon));
//            }
//            if (maxLon != null) {
//                predicates.add(criteriaBuilder.lessThanOrEqualTo(longitude, maxLon));
//            }
//            if (minLat != null) {
//                predicates.add(criteriaBuilder.greaterThanOrEqualTo(latitude, minLat));
//            }
//            if (maxLat != null) {
//                predicates.add(criteriaBuilder.lessThanOrEqualTo(latitude, maxLat));
//            }

            if (minLon != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("longitude"), minLon));
            }
            if (maxLon != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("longitude"), maxLon));
            }
            if (minLat != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("latitude"), minLat));
            }
            if (maxLat != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("latitude"), maxLat));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
