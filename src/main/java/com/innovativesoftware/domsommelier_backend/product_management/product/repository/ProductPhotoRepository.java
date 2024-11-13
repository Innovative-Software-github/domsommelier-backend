package com.innovativesoftware.domsommelier_backend.product_management.product.repository;


import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductPhoto;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.ProductPhotoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, String> {
    @Query("""
        select product.id as id, productPhoto.bucket as bucket, productPhoto.name as fileName from Product product
        inner join ProductPhoto productPhoto on product.id = productPhoto.product.id
        where product.productCategory.name = :cat
    """)
    List<ProductPhotoProjection> findAllProductPhotosFor(@Param("cat") ProductCategories category);
}