package com.innovativesoftware.domsommelier_backend.filter_management.model.types;

import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@SuperBuilder
public class MultiSelectFilterDto extends FilterDto {
    private Option[] options;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String value;
        private String label;

        public Option(HashMap<String, Object> obj) {
            this.value = (String) obj.get("value");
            this.label = (String) obj.get("label");
        }
    }

    public MultiSelectFilterDto(HashMap<String, Object> obj) {
        super(obj);
        Object optionsObj = obj.get("options");
        if (optionsObj instanceof List<?> optionsList) {
            this.options = optionsList.stream()
                    .filter(x -> x instanceof Map)
                    .map(x -> new MultiSelectFilterDto.Option((HashMap<String, Object>) x))
                    .toArray(MultiSelectFilterDto.Option[]::new);
        } else {
            this.options = null;
        }
    }
}
