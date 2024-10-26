package com.innovativesoftware.domsommelier_backend.product.repository;

import com.innovativesoftware.domsommelier_backend.entity.ProductCountry;
import com.innovativesoftware.domsommelier_backend.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product.model.ProductCountryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCountryRepository extends JpaRepository<ProductCountry, String> {
    @Query("""
        select distinct productCountry.name as name from ProductCountry productCountry inner join Product product
        on productCountry.name = product.productCountry.name
        where product.productCategory.name = :productCategory
    """)
    List<ProductCountryProjection> getCountriesWithProductCategory(ProductCategories productCategory);
}