package com.innovativesoftware.domsommelier_backend.filter_management.model.types;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class CheckboxFilterDto extends FilterDto {
    public CheckboxFilterDto(HashMap<String, Object> obj) {
        super(obj);
    }
}
