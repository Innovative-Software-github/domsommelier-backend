package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoCreateRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoRequest;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDtoResponse;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterOptionRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.util.FilterMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategories;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
        // Проверяем что в данной категории отсутствует такой же name или field
        if (filterRepository.existsByNameAndProductCategories(dto.getName(), dto.getProductCategory())) {
            throw new InvalidValueException("Фильтр с таким именем уже существует",
                    "INVALID_NAME",
                    "Фильтр с таким именем уже существует"
            );
        } else if (filterRepository.existsByFieldAndProductCategories(dto.getField(), dto.getProductCategory())) {
            throw new InvalidValueException("Фильтр с таким полем уже существует",
                    "INVALID_FIELD",
                    "Фильтр с таким полем уже существует"
            );
        }
        Filter saved = filterRepository.save(FilterMapper.toEntityCreate(dto));

        List<FilterOption> options = new ArrayList<>();

        if (dto.getFilterType() == FilterType.LIST) { // || dto.getFilterType() == FilterType.STRING) {
            options = dto.getOptions().stream()
                    .map(option -> FilterOption.builder().value(option).filter(saved).build())
                    .toList();
            filterOptionRepository.saveAll(options);
        }
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

    public List<UUID> getFiltersByProductCategory(ProductCategories productCategory) {
        return filterRepository.findByProductCategories(productCategory).stream().map(Filter::getId).toList();
    }

    public List<String> getFilterTypes() {
        return Arrays.stream(FilterType.values()).map(FilterType::name).toList();
    }
}
