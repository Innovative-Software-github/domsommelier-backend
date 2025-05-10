package com.innovativesoftware.domsommelier_backend.filter_management.util;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.FilterOption;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.ProductFilterValue;
import com.innovativesoftware.domsommelier_backend.filter_management.model.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.entity.Product;

import java.util.stream.Collectors;

public class FilterMapper {

    public static FilterDtoResponse toDTO(Filter entity) {
        if(entity == null) return null;
        return FilterDtoResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .field(entity.getField())
                .type(entity.getType())
                .productCategory(entity.getProductCategory())
                .options(entity.getOptions() != null
                        ? entity.getOptions().stream().map(FilterMapper::toDTO).collect(Collectors.toList())
                        : null)
                .build();
    }

    public static Filter toEntityCreate(FilterDtoRequest dto) {
        if(dto == null) return null;
        Filter filter = new Filter();
        filter.setName(dto.getName());
        filter.setField(dto.getField());
        filter.setType(dto.getFilterType());
        filter.setProductCategory(dto.getProductCategory());
        // options не выставляем (их ассоциируем отдельно)
        return filter;
    }

    public static Filter toEntityUpdate(FilterDtoRequest dto) {
        if(dto == null) return null;
        Filter filter = new Filter();
        filter.setId(dto.getId());
        filter.setName(dto.getName());
        filter.setField(dto.getField());
        filter.setType(dto.getFilterType());
        filter.setProductCategory(dto.getProductCategory());
        // options не выставляем (их ассоциируем отдельно)
        return filter;
    }

    public static FilterOptionDtoResponse toDTO(FilterOption entity) {
        if(entity == null) return null;
        return FilterOptionDtoResponse.builder()
                .id(entity.getId())
                .value(entity.getValue())
                .filterId(entity.getFilter() != null ? entity.getFilter().getId() : null)
                .filterName(entity.getFilter() != null ? entity.getFilter().getName() : null)
                .build();
    }

    public static FilterOption toEntityCreate(FilterOptionDtoRequest dto, Filter filter) {
        if(dto == null) return null;
        return FilterOption.builder()
                .value(dto.getValue())
                .filter(filter)
                .build();
    }

    public static FilterOption toEntityUpdate(FilterOptionDtoRequest dto, Filter filter) {
        if(dto == null) return null;
        return FilterOption.builder()
                .id(dto.getId())
                .value(dto.getValue())
                .filter(filter)
                .build();
    }

    public static ProductFilterValueDtoResponse toDTO(ProductFilterValue entity) {
        if(entity == null) return null;
        Product product = entity.getProduct();
        Filter filter = entity.getFilter();
        return ProductFilterValueDtoResponse.builder()
                .id(entity.getId())
                .optionId(entity.getOption().getId())
                .productId(product != null ? product.getId() : null)
                .productName(product != null ? product.getName() : null)
                .filterId(filter != null ? filter.getId() : null)
                .filterName(filter != null ? filter.getName() : null)
                .build();
    }
}
