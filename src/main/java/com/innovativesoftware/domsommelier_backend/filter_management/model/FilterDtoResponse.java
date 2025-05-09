package com.innovativesoftware.domsommelier_backend.filter_management.model;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDtoResponse {
    private UUID id;
    private String name;
    private String field;
    private String productCategory;
    private FilterType type;
    private List<FilterOptionDtoResponse> options;
}
