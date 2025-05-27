package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FilterRepository extends JpaRepository<Filter, UUID> {

    @Query("SELECT f FROM Filter f WHERE f.name = :name")
    Filter findByName(String name);

    @Query("SELECT f FROM Filter f WHERE f.productCategoryEnum = :productCategoryEnum")
    List<Filter> findByProductCategories(ProductCategoryEnum productCategoryEnum);

    @Query("SELECT EXISTS(SELECT 1 FROM Filter f WHERE f.name = :name AND f.productCategoryEnum = :productCategoryEnum)")
    boolean existsByNameAndProductCategories(String name, ProductCategoryEnum productCategoryEnum);

//    @Query("SELECT EXISTS(SELECT 1 FROM Filter f WHERE f.field = :field AND f.productCategory = :productCategory)")
//    boolean existsByFieldAndProductCategories(String field, ProductCategory productCategory);
}
