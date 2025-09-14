package com.innovativesoftware.domsommelier_backend.order_management.discount.repository;

import com.innovativesoftware.domsommelier_backend.order_management.discount.entity.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PromoRepository extends JpaRepository<Promo, UUID> {}
