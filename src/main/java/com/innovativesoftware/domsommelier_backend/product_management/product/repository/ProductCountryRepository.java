package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.ProductCountry;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductCountryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductCountryRepository extends JpaRepository<ProductCountry, String> {
    @Query("""
        select distinct productCountry.name as name from ProductCountry productCountry inner join Product product
        on productCountry.name = product.productCountry.name
        where product.productCategory.name = :productCategoryEnum
    """)
    List<ProductCountryProjection> getCountriesWithProductCategory(ProductCategoryEnum productCategoryEnum);
}