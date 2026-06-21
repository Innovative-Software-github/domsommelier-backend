package com.innovativesoftware.domsommelier_backend.geo_management.city.repository;

import com.innovativesoftware.domsommelier_backend.geo_management.city.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findAllByActiveTrueOrderBySortOrderAscNameAsc();

    List<City> findAllByOrderBySortOrderAscNameAsc();

    Optional<City> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
