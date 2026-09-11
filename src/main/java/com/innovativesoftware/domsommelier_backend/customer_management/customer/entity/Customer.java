package com.innovativesoftware.domsommelier_backend.customer_management.customer.entity;

import com.innovativesoftware.domsommelier_backend.shared.domain.Role;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;
import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "customer")
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    // Основная винотека для самовывоза
    @ManyToOne
    @JoinColumn(name = "default_wine_store_id")
    private WineStore defaultWineStore;

    // Избранные товары
    @ManyToMany
    @JoinTable(
            name = "customer_favorite_product",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> favoriteProducts = new ArrayList<>();

    /**
     * Личная скидка клиента в процентах (0..100). {@code null} — скидки нет.
     * Nullable намеренно: {@code ddl-auto: update} добавляет колонку без default,
     * поэтому «нет скидки» трактуется как {@code null == 0} в {@code CustomerDiscountResolver}.
     */
    @Column(name = "discount_percent")
    private Integer discountPercent;

    /** Основание для скидки — видно только в админке. */
    @Column(name = "discount_comment")
    private String discountComment;

    @Column(name = "discount_updated_at")
    private OffsetDateTime discountUpdatedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.ROLE_USER;

    public Role getRoleOrDefault() {
        return role != null ? role : Role.ROLE_USER;
    }
}