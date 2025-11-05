package com.innovativesoftware.domsommelier_backend.product_management.store.repository;

import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface WineStoreRepository extends JpaRepository<WineStore, Long>, JpaSpecificationExecutor<WineStore> {
}