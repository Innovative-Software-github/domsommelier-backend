package com.innovativesoftware.domsommelier_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "iiko_id", nullable = false)
    private String iikoId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "second_name", nullable = false)
    private String secondNmae;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "birth_date")
    private OffsetDateTime birthDate;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;
}