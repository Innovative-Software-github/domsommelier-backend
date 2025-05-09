package com.innovativesoftware.domsommelier_backend.filter_management.repository;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface FilterRepository extends JpaRepository<Filter, UUID> {}
