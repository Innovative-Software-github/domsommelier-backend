package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface FilterOptionRepository extends JpaRepository<FilterOption, UUID> {
    List<FilterOption> findByFilterId(UUID id);

    @Query(value = "SELECT * FROM filter_option fo WHERE fo.value IN (:values)", nativeQuery = true)
    List<FilterOption> findAllByValues(List<String> values);

    @Query(value = """
            SELECT EXISTS(SELECT *
            FROM filter_option fo
            WHERE fo.value = :value AND fo.filter_id = :filterId)
            """, nativeQuery = true)
    boolean existsByFilterIdAndValue(UUID filterId, String value);
}
