package com.innovativesoftware.domsommelier_backend.filter_management.util;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.CheckboxFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.MultiSelectFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.RangeFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.CheckboxFilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.MultiSelectFilterDto;
import com.innovativesoftware.domsommelier_backend.filter_management.model.types.RangeFilterDto;

import java.util.Arrays;
import java.util.List;

public class FilterMapper {

    public static RangeFilter fromDto(RangeFilterDto dto, Filter filter) {
        List<RangeFilter.Step> steps = null;
        if (dto.getSteps() != null) {
            steps = Arrays.stream(dto.getSteps())
                    .map(s -> new RangeFilter.Step(s.getMin(), s.getMax(), s.getLabel()))
                    .toList();
        }

        return RangeFilter.builder()
                .id(dto.getId())
                .filter(filter)
                .min(dto.getMin())
                .max(dto.getMax())
                .unit(dto.getUnit())
                .steps(steps)
                .build();
    }

    public static CheckboxFilter fromDto(CheckboxFilterDto dto, Filter filter) {
        return CheckboxFilter.builder()
                .id(dto.getId())
                .filter(filter)
                .build();
    }

    public static MultiSelectFilter fromDto(MultiSelectFilterDto dto, Filter filter) {
        List<MultiSelectFilter.Option> options = null;
        if (dto.getOptions() != null) {
            options = Arrays.stream(dto.getOptions())
                    .map(o -> new MultiSelectFilter.Option(o.getValue(), o.getLabel()))
                    .toList();
        }

        return MultiSelectFilter.builder()
                .id(dto.getId())
                .options(options)
                .filter(filter)
                .build();
    }

    public static FilterDto toDto(CheckboxFilter filter) {
        return CheckboxFilterDto.builder().id(filter.getId())
                .category(filter.getFilter().getProductCategoryEnum()).name(filter.getFilter().getName())
                .field(filter.getFilter().getField()).type(filter.getFilter().getType()).build();
    }

    public static FilterDto toDto(MultiSelectFilter filter) {
        return MultiSelectFilterDto.builder().id(filter.getId())
                .category(filter.getFilter().getProductCategoryEnum()).name(filter.getFilter().getName())
                .field(filter.getFilter().getField())
                .type(filter.getFilter().getType()).options(
                        toOptionDto(filter.getOptions()).toArray(MultiSelectFilterDto.Option[]::new)).build();
    }

    private static List<MultiSelectFilterDto.Option> toOptionDto(List<MultiSelectFilter.Option> options) {
        return options.stream().map(o -> MultiSelectFilterDto.Option.
                builder().value(o.getVal()).label(o.getLabel()).build()).toList();
    }

    public static FilterDto toDto(RangeFilter filter) {
        return RangeFilterDto.builder().id(filter.getId())
                .field(filter.getFilter().getField())
                .category(filter.getFilter().getProductCategoryEnum()).name(filter.getFilter().getName())
                .type(filter.getFilter().getType()).min(filter.getMin()).max(filter.getMax())
                .unit(filter.getUnit()).steps(
                        toStepDto(filter.getSteps()).toArray(RangeFilterDto.Step[]::new)).build();
    }

    private static List<RangeFilterDto.Step> toStepDto(List<RangeFilter.Step> steps) {
        return steps.stream().map(s -> RangeFilterDto.Step.
                builder().min(s.getMin()).max(s.getMax()).label(s.getLabel()).build()).toList();
    }
}
