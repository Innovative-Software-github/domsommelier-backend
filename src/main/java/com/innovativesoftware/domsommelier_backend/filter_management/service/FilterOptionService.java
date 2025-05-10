package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterOptionDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterOptionDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterOptionRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.util.FilterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilterOptionService {
    private final FilterOptionRepository filterOptionRepository;
    private final FilterRepository filterRepository;

    public List<FilterOptionDtoResponse> getAll() {
        return filterOptionRepository.findAll().stream().map(FilterMapper::toDTO).toList();
    }

    public FilterOptionDtoResponse getById(UUID id) {
        return FilterMapper.toDTO(filterOptionRepository.findById(id).orElseThrow());
    }

    public FilterOptionDtoResponse create(FilterOptionDtoRequest dto) {
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();
        if (filterOptionRepository.existsByFilterIdAndValue(filter.getId(), dto.getValue())) {
            throw new IllegalArgumentException("Option name already exists in the filter");
        }
        FilterOption entity = FilterMapper.toEntityCreate(dto, filter);
        FilterOption saved = filterOptionRepository.save(entity);
        return FilterMapper.toDTO(saved);
    }

    public UUID delete(UUID id) {
        filterOptionRepository.deleteById(id);
        return id;
    }

    public FilterOptionDtoResponse update(UUID id, FilterOptionDtoRequest dto) {
        Filter filter = filterRepository.findById(dto.getFilterId()).orElseThrow();
        FilterOption entity = FilterMapper.toEntityUpdate(dto, filter);
        entity.setId(id);
        FilterOption saved = filterOptionRepository.save(entity);
        return FilterMapper.toDTO(saved);
    }

    public List<FilterOptionDtoResponse> getByFilterId(UUID filterId) {
        return filterOptionRepository.findByFilterId(filterId).stream().map(FilterMapper::toDTO).toList();
    }
}
