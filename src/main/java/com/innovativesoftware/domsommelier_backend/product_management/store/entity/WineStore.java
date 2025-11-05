package com.innovativesoftware.domsommelier_backend.product_management.store.entity;

import com.innovativesoftware.domsommelier_backend.product_management.store.utils.PointConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "wine_store")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class WineStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "location", nullable = false, columnDefinition = "point")
//    private Point location;
    @Column(name = "location", nullable = false, columnDefinition = "point")
    @Convert(converter = PointConverter.class)
    private WineStorePoint location;

    @Formula("(location[0])")
    private Double longitude;

    @Formula("(location[1])")
    private Double latitude;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district", nullable = false)
    private String district;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        WineStore wineStore = (WineStore) o;
        return getId() != null && Objects.equals(getId(), wineStore.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}