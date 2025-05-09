package com.innovativesoftware.domsommelier_backend.filter_management.model;

import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterDtoRequest {
    @Schema(description = "можно не указывать при создании")
    private UUID id;
    private String name;
    private String field;
    private String productCategory;
    private FilterType filterType;
    @Schema(description = "можно null или пустым списком для GET-списка всех фильтров")
    private List<FilterOptionDtoRequest> options;
}
