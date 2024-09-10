package com.innovativesoftware.domsommelier_backend.shared.model;

import com.innovativesoftware.domsommelier_backend.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class File<TDomain> {
    @Id
    @Column(name = "link", nullable = false)
    protected String link;

    @Column(name= "name")
    protected String name;

    @Column(name = "description")
    protected String description;

    @ManyToOne
    @JoinColumn(name = "domain_id")
    private TDomain domain;
}
