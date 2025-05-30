package com.innovativesoftware.domsommelier_backend.filter_management.model.types;

import com.innovativesoftware.domsommelier_backend.filter_management.model.FilterDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RangeFilterDto extends FilterDto {
    private Double min;
    private Double max;
    private String unit;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Step {
        public Double min;
        public Double max;
        public String label;

        public Step(HashMap<String, Object> obj) {
            this.min = (Double)obj.get("min");
            this.max = (Double)obj.get("max");
            this.label = (String)obj.get("label");
        }
    }

    private Step[] steps;

    public RangeFilterDto(HashMap<String, Object> obj) {
        super(obj);

        this.min = (Double)obj.get("min");
        this.max = (Double)obj.get("max");
        this.unit = (String)obj.get("unit");
        Object stepsObj = obj.get("steps");
        if (stepsObj instanceof List<?> stepsList) {
            this.steps = stepsList.stream()
                    .filter(x -> x instanceof Map)
                    .map(x -> new Step((HashMap<String, Object>) x))
                    .toArray(Step[]::new);
        } else {
            this.steps = null;
        }
    }
}
