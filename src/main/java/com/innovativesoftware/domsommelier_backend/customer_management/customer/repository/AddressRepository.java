package com.innovativesoftware.domsommelier_backend.customer_management.customer.repository;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> { }
