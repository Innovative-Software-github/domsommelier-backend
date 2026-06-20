package com.innovativesoftware.domsommelier_backend.event_management.event.entity;

import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "event")
public class Event {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "type", nullable = false)
    private String type;  // Винное казино/Дегустация

    @Column(name = "price")
    private Integer price;

    @Column(name = "datetime")
    private OffsetDateTime datetime;

    @Column(name = "title")
    private String title;

    @Column(name = "small_cover")
    private String smallCover;

    @Column(name = "large_cover")
    private String largeCover;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "registration_link")
    private String registrationLink;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "wine_store_id")
    private WineStore wineStore;
}
