package com.innovativesoftware.domsommelier_backend.product_management.product.attributes;

import com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes.CatalogAttributes.*;
import com.innovativesoftware.domsommelier_backend.product_management.product.model.write.*;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.lang.reflect.RecordComponent;
import java.util.*;

@Service @RequiredArgsConstructor
public class AttributeReferenceService {
    private final AttributeReferenceRepository repository;
    private final Validator validator;
    public static final Set<String> KINDS = Set.of("brand", "packaging", "region", "appellation", "grape",
        "aroma", "flavor", "food_pairing", "style", "vessel", "wood", "previous_contents", "serving",
        "sensory_dimension", "whisky_type", "blend_style", "age_classification", "origin_area", "sparkling_method");
    private static final Map<String, String> FIELDS = Map.ofEntries(
        Map.entry("brand", "brand"), Map.entry("type", "packaging"), Map.entry("region", "region"),
        Map.entry("appellation", "appellation"), Map.entry("grape", "grape"), Map.entry("aromaTags", "aroma"),
        Map.entry("flavorTags", "flavor"), Map.entry("foodPairingTags", "food_pairing"), Map.entry("styleTags", "style"),
        Map.entry("vessels", "vessel"), Map.entry("vessel", "vessel"), Map.entry("wood", "wood"),
        Map.entry("previousContents", "previous_contents"), Map.entry("servingTags", "serving"),
        Map.entry("dimension", "sensory_dimension"), Map.entry("whiskyType", "whisky_type"),
        Map.entry("blendStyle", "blend_style"), Map.entry("ageClassification", "age_classification"),
        Map.entry("originArea", "origin_area"), Map.entry("sparklingMethod", "sparkling_method"));

    public List<Reference> list(String kind) {
        checkKind(kind);
        return repository.findByKindOrderByLabelAsc(kind).stream().map(r -> new Reference(r.getCode(), r.getLabel())).toList();
    }

    @Transactional
    public Reference create(String kind, Reference value) {
        checkKind(kind);
        String label = value.label().trim();
        String id = kind + ":" + value.code();
        if (repository.existsById(id) || repository.existsByKindAndLabelIgnoreCase(kind, label)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Код или название уже есть в справочнике");
        }
        AttributeReference row = new AttributeReference();
        row.setId(id); row.setKind(kind); row.setCode(value.code()); row.setLabel(label);
        try { repository.saveAndFlush(row); }
        catch (org.springframework.dao.DataIntegrityViolationException conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Код или название уже есть в справочнике");
        }
        return new Reference(row.getCode(), row.getLabel());
    }

    /** Old clients may omit all new fields. Supplied fields must be valid and canonical. */
    public void validate(ProductWriteRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).sorted().findFirst().orElse("Неверные характеристики"));
        check(request.getBrand(), "brand");
        check(request.getPackaging(), "packaging");
        if (request instanceof WineWriteRequest wine) check(wine.getExtendedDetails(), "details");
        if (request instanceof SparklingWriteRequest sparkling) check(sparkling.getExtendedDetails(), "details");
        if (request instanceof SpiritWriteRequest spirit) {
            var details = spirit.getExtendedDetails();
            check(details, "details");
            if (details != null) validateSpiritSubtype(spirit.getSubcategory(), details);
        }
    }

    public static void validateSpiritSubtype(String subcategory, SpiritAttributes details) {
        String value = subcategory == null ? "" : subcategory.trim().toLowerCase(Locale.ROOT);
        if (details.whiskyDetails() != null && !Set.of("виски", "whisky", "whiskey").contains(value))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Характеристики виски доступны только подкатегории Виски");
        if (details.cognacDetails() != null && !Set.of("коньяк", "cognac").contains(value))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Характеристики коньяка доступны только подкатегории Коньяк");
    }

    private void check(Object value, String field) {
        if (value == null) return;
        if (value instanceof Reference ref) {
            String kind = FIELDS.get(field);
            if (kind == null) throw new IllegalStateException("Missing dictionary mapping: " + field);
            var row = repository.findById(kind + ":" + ref.code()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Значение не найдено в справочнике " + kind + ": " + ref.code()));
            if (!row.getLabel().equals(ref.label())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Название не соответствует коду справочника " + kind + ": " + ref.code());
        } else if (value instanceof List<?> list) {
            Set<String> codes = new HashSet<>();
            for (Object item : list) {
                if (item instanceof Reference ref && !codes.add(ref.code()))
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Повтор значения: " + field);
                check(item, field);
            }
        } else if (value.getClass().isRecord()) {
            for (RecordComponent component : value.getClass().getRecordComponents()) {
                try { check(component.getAccessor().invoke(value), component.getName()); }
                catch (ReflectiveOperationException e) { throw new IllegalStateException(e); }
            }
        }
    }

    private void checkKind(String kind) {
        if (!KINDS.contains(kind)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неизвестный справочник");
    }
}
