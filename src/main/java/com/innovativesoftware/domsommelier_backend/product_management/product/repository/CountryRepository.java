package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Country;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.CountryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, String> {
    @Query("""
        select distinct country.name as name from Country country inner join Product product
        on country.id = product.country.id
        where product.productCategory.name = :productCategory
    """)
    List<CountryProjection> getCountriesWithProductCategory(ProductCategories productCategory);
}