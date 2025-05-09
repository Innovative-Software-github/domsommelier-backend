package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FilterOptionRepository extends JpaRepository<FilterOption, UUID> {
    List<FilterOption> findByFilterId(UUID id);
}
