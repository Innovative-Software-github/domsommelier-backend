package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.RangeFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RangeFilterRepository extends JpaRepository<RangeFilter, UUID> {

    //Optional<RangeFilter> findById(UUID filterId);
}
