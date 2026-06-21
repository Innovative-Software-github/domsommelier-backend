package com.innovativesoftware.domsommelier_backend.product_management.product.repository;

import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    /*@Query("SELECT p.id FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :name, '%'))")
    List<UUID> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("SELECT p.id FROM Product p WHERE p.productCategory.name = :category")
    List<UUID> findByProductCategory(@Param("category") ProductCategoryEnum category);

    @Query("SELECT p.id FROM Product p WHERE p.productCountry.name = :country")
    List<UUID> findByProductCountry(@Param("country") String country);

    @Query("SELECT p.id FROM Product p WHERE LOWER(p.name) LIKE LOWER(concat('%', :name, '%')) AND p.productCategory.name = :category")
    List<UUID> findByNameContainingIgnoreCaseAndProductCategory(@Param("name") String name, @Param("category") String category);

    @NotNull Optional<Product> findById(@NotNull UUID productId);
    @NotNull List<Product> findAll();

    @Query("SELECT p.id FROM Product p")
    List<UUID> findAllIds();*/

    @Query("SELECT p.id FROM Product p WHERE p.productCategory.name = :category")
    Page<UUID> findByProductCategory(@Param("category") ProductCategoryEnum category, Pageable pageable);

    @Query("SELECT p.id FROM Product p WHERE p.productCountry.name = :country")
    Page<UUID> findByProductCountry(@Param("country") String country, Pageable pageable);

    @Query("SELECT p.id FROM Product p")
    Page<UUID> findAllIds(Pageable pageable);

    Page<Product> findAll(Pageable pageable);

    // search — непустая строка (пустая означает «без фильтра»: LIKE '%%' matchает всё).
    // Не используем «:search IS NULL», т.к. null-параметр в LOWER/LIKE ломает биндинг типа в Postgres.
    @Query(value = """
            SELECT p FROM Product p JOIN FETCH p.productCategory
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(p.article) LIKE LOWER(CONCAT('%', :search, '%'))
            """,
            countQuery = """
            SELECT COUNT(p) FROM Product p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(p.article) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Product> searchForStock(@Param("search") String search, Pageable pageable);

    // Админский список товаров категории с поиском по названию/артикулу (search — непустая строка).
    @Query(value = """
            SELECT p FROM Product p JOIN FETCH p.productCategory
            WHERE p.productCategory.name = :category
              AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.article) LIKE LOWER(CONCAT('%', :search, '%')))
            """,
            countQuery = """
            SELECT COUNT(p) FROM Product p
            WHERE p.productCategory.name = :category
              AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.article) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Product> searchProductsByCategory(@Param("category") ProductCategoryEnum category,
                                           @Param("search") String search,
                                           Pageable pageable);

    @Query("SELECT DISTINCT p.productCountry.name FROM Product p")
    Set<String> findDistinctCountries();

    @Query("""
        SELECT DISTINCT p.productCountry.name
        FROM Product p
        WHERE p.productCategory.name = :category
    """)
    Set<String> findDistinctCountriesByCategory(@Param("category") String category);

    /** Снять товар из избранного у всех клиентов (очистка join-таблицы перед удалением товара). */
    @Modifying
    @Query(value = "DELETE FROM customer_favorite_product WHERE product_id = :productId", nativeQuery = true)
    void deleteFavoritesByProductId(@Param("productId") UUID productId);

}