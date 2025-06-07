package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.exceptions.InvalidValueException;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.CheckboxFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.MultiSelectFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.RangeFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.CheckboxFilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.MultiSelectFilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.RangeFilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.CheckboxFilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.MultiSelectFilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.RangeFilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.util.FilterMapper;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilterService {
    private final FilterRepository filterRepository;
    private final CheckboxFilterRepository checkboxFilterRepository;
    private final MultiSelectFilterRepository multiSelectFilterRepository;
    private final RangeFilterRepository rangeFilterRepository;

    @Transactional(readOnly = true)
    public Map<String, Map<String, FilterDto>> getAllFilters() {
        List<Filter> allFilters = filterRepository.findAll();

        Map<FilterType, List<UUID>> filterIdsByType = allFilters.stream()
                .collect(Collectors.groupingBy(Filter::getType, Collectors.mapping(Filter::getId, Collectors.toList())));

        List<FilterDto> filters = new ArrayList<>();

        filterIdsByType.getOrDefault(FilterType.checkbox, List.of()).forEach(id ->
                filters.add(FilterMapper.toDto(checkboxFilterRepository.getReferenceById(id)))
        );
        filterIdsByType.getOrDefault(FilterType.multi_select, List.of()).forEach(id ->
                filters.add(FilterMapper.toDto(multiSelectFilterRepository.getReferenceById(id)))
        );
        filterIdsByType.getOrDefault(FilterType.range, List.of()).forEach(id ->
                filters.add(FilterMapper.toDto(rangeFilterRepository.getReferenceById(id)))
        );

        return filters.stream()
                .collect(Collectors.groupingBy(
                        filterDto -> filterDto.getCategory().name(),
                        LinkedHashMap::new,
                        Collectors.toMap(
                                FilterDto::getName, // имя фильтра как ключ
                                f -> f,             // объект фильтра как значение
                                (f1, f2) -> f1,     // если совпадения по названию - взять первый
                                LinkedHashMap::new
                        )
                ));
    }


    @Transactional(readOnly = true)
    public FilterDto getById(UUID id) {
        switch (filterRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Фильтр с таким идентификатором не найден")
        ).getType()) {
            case checkbox -> {
                CheckboxFilter checkboxFilter = checkboxFilterRepository.findById(id).get();
                return FilterMapper.toDto(checkboxFilter);
            }
            case multi_select -> {
                MultiSelectFilter multiSelectFilter = multiSelectFilterRepository.findById(id).get();
                return FilterMapper.toDto(multiSelectFilter);
            }
            case range -> {
                RangeFilter rangeFilter = rangeFilterRepository.findById(id).get();
                return FilterMapper.toDto(rangeFilter);
            }
            default -> throw new RuntimeException("Неизвестный тип фильтра");
        }
    }

    @Transactional
    public UUID create(HashMap<String, Object> obj) {
        FilterDto dto;

        switch (FilterType.valueOf(obj.get("type").toString())) {
            case range -> dto = new RangeFilterDto(obj);
            case checkbox -> dto = new CheckboxFilterDto(obj);
            case multi_select -> dto = new MultiSelectFilterDto(obj);
            default -> throw new RuntimeException("Неизвестный тип фильтра");
        }

        dto.setName(obj.get("name").toString());
        dto.setCategory(ProductCategoryEnum.valueOf(obj.get("category").toString()));
        dto.setType(FilterType.valueOf(obj.get("type").toString()));

        // Проверяем что в данной категории отсутствует такой же name или field
        if (filterRepository.existsByNameAndProductCategories(dto.getName(), dto.getCategory())) {
            throw new InvalidValueException("Фильтр с таким именем уже существует",
                    "INVALID_NAME",
                    "Фильтр с таким именем уже существует"
            );
        }

        Filter filter = Filter.builder().name(dto.getName()).type(dto.getType())
                .productCategoryEnum(dto.getCategory()).build();
        UUID id = filterRepository.save(filter).getId();

        switch (dto.getType()) {
            case range -> rangeFilterRepository.save(FilterMapper.fromDto((RangeFilterDto) dto, filter));
            case checkbox -> checkboxFilterRepository.save(FilterMapper.fromDto((CheckboxFilterDto) dto, filter));
            case multi_select -> multiSelectFilterRepository.save(FilterMapper.fromDto((MultiSelectFilterDto) dto, filter));
            default -> throw new RuntimeException("Неизвестный тип фильтра");
        }

        return id;
    }

    @Transactional
    public UUID delete(UUID id) {
        var filter = filterRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Фильтр с таким идентификатором не найден")
        );
        FilterType type = filter.getType();

        switch (type) {
            case range -> rangeFilterRepository.deleteById(id);
            case checkbox -> checkboxFilterRepository.deleteById(id);
            case multi_select -> multiSelectFilterRepository.deleteById(id);
        }

        filterRepository.deleteById(id);
        return id;
    }

    @Transactional
    public FilterDto update(UUID id, Map<String, Object> dto) {
        Filter filter = filterRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Фильтр с таким идентификатором не найден")
        );
        filter.setName(dto.get("name").toString());
        filter.setType(FilterType.valueOf(dto.get("type").toString()));
        filter.setProductCategoryEnum(ProductCategoryEnum.valueOf(dto.get("category").toString()));

        switch (filter.getType()) {
            case range -> {
                RangeFilter rangeFilter = rangeFilterRepository.findById(id).orElseThrow(
                        () -> new NoSuchElementException("Фильтр с таким идентификатором не найден")
                );
                rangeFilter.setFilter(filter);
                rangeFilter.setMin(Double.valueOf(dto.get("min").toString()));
                rangeFilter.setMax(Double.valueOf(dto.get("max").toString()));
                rangeFilter.setUnit(dto.get("unit").toString());
                Object stepsObj = dto.get("steps");
                if (stepsObj instanceof List<?> stepsList) {
                    List<RangeFilter.Step> steps = stepsList.stream()
                            .filter(item -> item instanceof Map)
                            .map(item -> {
                                Map<String, Object> map = (Map<String, Object>) item;
                                Double min = map.get("min") != null ? Double.valueOf(map.get("min").toString()) : null;
                                Double max = map.get("max") != null ? Double.valueOf(map.get("max").toString()) : null;
                                String label = (String) map.get("label");
                                return new RangeFilter.Step(min, max, label);
                            })
                            .collect(Collectors.toCollection(ArrayList::new));
                    rangeFilter.setSteps(steps);
                } else {
                    rangeFilter.setSteps(null);
                }
                rangeFilterRepository.save(rangeFilter);
                return FilterMapper.toDto(rangeFilter);
            }
            case checkbox -> {
                CheckboxFilter checkboxFilter = checkboxFilterRepository.getReferenceById(filter.getId());
                checkboxFilter.setFilter(filter);
                checkboxFilterRepository.save(checkboxFilter);
                return FilterMapper.toDto(checkboxFilter);
            }
            case multi_select -> {
                MultiSelectFilter multiSelectFilter = multiSelectFilterRepository.findById(filter.getId())
                        .orElseThrow(() -> new NoSuchElementException("Фильтр с таким идентификатором не найден"));
                multiSelectFilter.setFilter(filter);

                Object optionsObj = dto.get("options");
                if (optionsObj instanceof List<?> optionsList) {
                    List<MultiSelectFilter.Option> options = optionsList.stream()
                            .filter(item -> item instanceof Map)
                            .map(item -> {
                                Map<String, Object> map = (Map<String, Object>) item;
                                String value = (String) map.get("value");
                                String label = (String) map.get("label");
                                return new MultiSelectFilter.Option(value, label);
                            })
                            .collect(Collectors.toCollection(ArrayList::new));
                    multiSelectFilter.setOptions(options);
                } else {
                    multiSelectFilter.setOptions(null);
                }
                multiSelectFilterRepository.save(multiSelectFilter);
                return FilterMapper.toDto(multiSelectFilter);
            }

            default ->
                    throw new InvalidValueException("Неизвестный тип фильтра", "INVALID_TYPE", "Неизвестный тип фильтра");
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Map<String, FilterDto>> getFiltersByProductCategory(ProductCategoryEnum productCategoryEnum) {
        List<Filter> allFilters = filterRepository.findByProductCategories(productCategoryEnum);

        Map<FilterType, List<UUID>> filterIdsByType = allFilters.stream()
                .collect(Collectors.groupingBy(Filter::getType, Collectors.mapping(Filter::getId, Collectors.toList())));

        List<FilterDto> filters = new ArrayList<>();

        filterIdsByType.getOrDefault(FilterType.checkbox, List.of()).forEach(id ->
                filters.add(FilterMapper.toDto(checkboxFilterRepository.getReferenceById(id)))
        );
        filterIdsByType.getOrDefault(FilterType.multi_select, List.of()).forEach(id ->
                filters.add(FilterMapper.toDto(multiSelectFilterRepository.getReferenceById(id)))
        );
        filterIdsByType.getOrDefault(FilterType.range, List.of()).forEach(id ->
                filters.add(FilterMapper.toDto(rangeFilterRepository.getReferenceById(id)))
        );

        return filters.stream()
                .collect(Collectors.groupingBy(
                        filterDto -> filterDto.getCategory().name(),
                        LinkedHashMap::new,
                        Collectors.toMap(
                                FilterDto::getName, // имя фильтра как ключ
                                f -> f,             // объект фильтра как значение
                                (f1, f2) -> f1,     // если совпадения по названию - взять первый
                                LinkedHashMap::new
                        )
                ));
    }

    @Transactional(readOnly = true)
    public List<String> getFilterTypes() {
        return Arrays.stream(FilterType.values()).map(FilterType::name).toList();
    }

    @Transactional(readOnly = true)
    public FilterDto getByName(String name, ProductCategoryEnum category) {
        Filter filter = filterRepository.findByNameAndProductCategories(name, category);
        if (filter == null)
            throw new NoSuchElementException("Фильтр с таким именем не найден");
        UUID id = filter.getId();
        switch (filter.getType()) {
            case checkbox -> {
                CheckboxFilter checkboxFilter = checkboxFilterRepository.findById(id).orElseThrow(
                        () -> new NoSuchElementException("Фильтр с таким идентификатором не найден")
                );
                return FilterMapper.toDto(checkboxFilter);
            }
            case multi_select -> {
                MultiSelectFilter multiSelectFilter = multiSelectFilterRepository.findById(id).orElseThrow(
                        () -> new NoSuchElementException("Фильтр с таким идентификатором не найден")
                );
                return FilterMapper.toDto(multiSelectFilter);
            }
            case range -> {
                RangeFilter rangeFilter = rangeFilterRepository.findById(id).orElseThrow(
                        () -> new NoSuchElementException("Фильтр с таким идентификатором не найден")
                );
                return FilterMapper.toDto(rangeFilter);
            }
            default -> throw new RuntimeException("Неизвестный тип фильтра");
        }
    }

    public List<FilterDto> getByCategory(ProductCategoryEnum categoryName) {
        var filters = filterRepository.findByProductCategories(categoryName);
        return filters.stream().map(filter -> {
            switch (filter.getType()) {
                case range -> {
                    return FilterMapper.toDto(rangeFilterRepository.findById(filter.getId()).orElseThrow());
                }
                case checkbox -> {
                    return FilterMapper.toDto(checkboxFilterRepository.findById(filter.getId()).orElseThrow());
                }
                case multi_select -> {
                    return FilterMapper.toDto(multiSelectFilterRepository.findById(filter.getId()).orElseThrow());
                }
                default -> {
                    return null;
                }
            }
        }).toList();
    }
}
