package com.innovativesoftware.domsommelier_backend.product_management.product.repository;


import com.innovativesoftware.domsommelier_backend.file_management.model.FileDTO;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.ProductPhoto;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, String> {
    @Query("""
        select product.id as id, productPhoto.bucket as bucket, productPhoto.name as fileName from Product product
        inner join ProductPhoto productPhoto on product.id = productPhoto.product.id
        where product.productCategory.name = :cat
    """)
    List<FileDTO> findAllProductPhotosFor(@Param("cat") ProductCategoryEnum category);

    List<ProductPhoto> findByProduct_Id(UUID productId);

    // id фото — UUID (тип в JpaRepository указан как String исторически), поэтому ищем явным запросом.
    @Query("select p from ProductPhoto p where p.id = :id")
    Optional<ProductPhoto> findByPhotoId(@Param("id") UUID id);
}