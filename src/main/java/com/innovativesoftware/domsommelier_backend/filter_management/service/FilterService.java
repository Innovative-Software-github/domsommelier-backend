package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoCreateRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterOptionRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.util.FilterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final FilterRepository filterRepository;
    private final FilterOptionRepository filterOptionRepository;

    public List<FilterDtoResponse> getAllFilters() {
        return filterRepository.findAll().stream()
                .map(FilterMapper::toDTO)
                .toList();
    }

    public FilterDtoResponse getById(UUID id) {
        return FilterMapper.toDTO(filterRepository.findById(id).orElseThrow());
    }

    public FilterDtoResponse create(FilterDtoCreateRequest dto) {
        Filter saved = filterRepository.save(FilterMapper.toEntityCreate(dto));
        List<FilterOption> options = dto.getOptions().stream().map(
                option -> FilterOption.builder().value(option).filter(saved).build()
        ).toList();

        filterOptionRepository.saveAll(options);
        saved.setOptions(options);
        return FilterMapper.toDTO(saved);
    }

    public UUID delete(UUID id) {
        filterRepository.deleteById(id);
        return id;
    }

    public FilterDtoResponse update(UUID id, FilterDtoRequest dto) {
        Filter entity = FilterMapper.toEntityUpdate(dto);
        entity.setId(id);
        Filter saved = filterRepository.save(entity);
        return FilterMapper.toDTO(saved);
    }
}
