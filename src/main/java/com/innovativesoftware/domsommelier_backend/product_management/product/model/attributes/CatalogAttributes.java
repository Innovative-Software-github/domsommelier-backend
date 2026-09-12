package com.innovativesoftware.domsommelier_backend.product_management.product.model.attributes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/** Typed optional attributes. Null denotes unknown, never an inferred zero. */
public final class CatalogAttributes {
    private CatalogAttributes() {}

    public enum AgingStatus { aged, not_aged, unknown }
    public enum VintageStatus { vintage, non_vintage, unknown }
    public enum Aeration { recommended, not_recommended, optional }
    public enum StorageReference { vintage, bottling, purchase, unspecified }
    public enum AgingPurpose { primary, finish, unspecified }
    public enum AgeStatementStatus { stated, nas, unknown }

    public record Reference(@NotBlank @Pattern(regexp = "[a-z0-9][a-z0-9_-]{0,79}") String code,
                            @NotBlank @Size(max = 160) String label) {}
    public record Packaging(Boolean giftBox, @Valid Reference type) {}
    public record GrapeShare(@NotNull @Valid Reference grape,
                             @DecimalMin("0") @DecimalMax("100") BigDecimal percent) {}
    public record SourceRating(@NotNull @DecimalMin("0") BigDecimal value,
                               @DecimalMin("0") BigDecimal scaleMin,
                               @DecimalMin("0") BigDecimal scaleMax,
                               @NotBlank @Size(max = 200) String sourceId) {
        @JsonIgnore @AssertTrue(message = "Оценка должна соответствовать границам шкалы")
        public boolean isRangeValid() {
            return (scaleMin == null || scaleMax == null || scaleMin.compareTo(scaleMax) < 0)
                && (value == null || scaleMin == null || value.compareTo(scaleMin) >= 0)
                && (value == null || scaleMax == null || value.compareTo(scaleMax) <= 0);
        }
    }
    public record SensoryProfile(@Valid SourceRating sweetness, @Valid SourceRating acidity,
        @Valid SourceRating aromaticIntensity, @Valid SourceRating body, @Valid SourceRating tannins) {}
    public record SensoryDimension(@NotNull @Valid Reference dimension,
                                   @NotNull @Valid SourceRating rating) {}
    public record DurationRange(@DecimalMin("0") BigDecimal min, @DecimalMin("0") BigDecimal max,
                                @NotNull Boolean minInclusive, @NotNull Boolean maxInclusive) {
        @JsonIgnore @AssertTrue(message = "Укажите допустимые границы срока выдержки")
        public boolean isRangeValid() {
            return (min != null || max != null) && (min == null || max == null ||
                min.compareTo(max) < 0 || (min.compareTo(max) == 0 && Boolean.TRUE.equals(minInclusive)
                    && Boolean.TRUE.equals(maxInclusive)));
        }
    }
    public record Aging(@NotNull AgingStatus status,
                        @Size(max = 30) List<@NotNull @Valid Reference> vessels,
                        @Valid DurationRange durationMonths, @Size(max = 4000) String description) {
        @JsonIgnore @AssertTrue(message = "Для отсутствующей выдержки срок и ёмкости не задаются")
        public boolean isStatusValid() {
            return status != AgingStatus.not_aged || (durationMonths == null && (vessels == null || vessels.isEmpty()));
        }
    }
    public record AgingStage(@NotNull @Min(1) Integer order, @NotNull AgingPurpose purpose,
        @Valid Reference vessel, @Valid Reference wood, @Valid Reference previousContents,
        @Valid DurationRange durationMonths) {}
    public record TemperatureRange(@NotNull @DecimalMin("-20") @DecimalMax("100") BigDecimal min,
                                   @NotNull @DecimalMin("-20") @DecimalMax("100") BigDecimal max) {
        @JsonIgnore @AssertTrue(message = "Минимальная температура не может превышать максимальную")
        public boolean isRangeValid() { return min == null || max == null || min.compareTo(max) <= 0; }
    }
    public record CellaringPotential(@NotNull @Min(0) @Max(200) Integer minYears,
        @NotNull @Min(0) @Max(200) Integer maxYears, @NotNull StorageReference reference) {
        @JsonIgnore @AssertTrue(message = "Проверьте диапазон потенциала хранения")
        public boolean isRangeValid() { return minYears == null || maxYears == null || minYears <= maxYears; }
    }
    public record WhiskySpecific(@Valid Reference whiskyType, @Valid Reference blendStyle,
        @Min(1) @Max(1000) Integer componentCount, @Min(1) @Max(200) Integer ageStatementYears,
        @NotNull AgeStatementStatus ageStatementStatus) {
        @JsonIgnore @AssertTrue(message = "Возраст указывается только вместе со статусом stated")
        public boolean isAgeValid() {
            return ageStatementStatus == AgeStatementStatus.stated ? ageStatementYears != null : ageStatementYears == null;
        }
    }
    public record CognacSpecific(@Valid Reference ageClassification,
        @Min(1) @Max(200) Integer ageStatementYears,
        @Size(max = 30) List<@NotNull @Valid GrapeShare> grapeComposition,
        Boolean grapeCompositionComplete, @Valid Reference originArea) {
        @JsonIgnore @AssertTrue(message = "Проверьте доли и повторения сортов винограда")
        public boolean isCompositionValid() { return compositionValid(grapeComposition, grapeCompositionComplete); }
    }

    private static boolean compositionValid(List<GrapeShare> shares, Boolean complete) {
        if (shares == null || shares.isEmpty()) return !Boolean.TRUE.equals(complete);
        var codes = new java.util.HashSet<String>();
        BigDecimal sum = BigDecimal.ZERO;
        for (var share : shares) {
            if (share == null || share.grape() == null) continue; // @Valid reports the required value
            if (!codes.add(share.grape().code())) return false;
            if (share.percent() == null && Boolean.TRUE.equals(complete)) return false;
            if (share.percent() != null) sum = sum.add(share.percent());
        }
        return sum.compareTo(BigDecimal.valueOf(100)) <= 0 &&
            (!Boolean.TRUE.equals(complete) || sum.compareTo(BigDecimal.valueOf(100)) == 0);
    }

    public record WineAttributes(
        @Valid Reference region,
        @Valid Reference appellation,
        @Size(max = 40) List<@NotNull @Valid Reference> aromaTags,
        @Size(max = 40) List<@NotNull @Valid Reference> flavorTags,
        @Size(max = 40) List<@NotNull @Valid Reference> foodPairingTags,
        @Valid Aging aging,
        @Size(max = 4000) String productionMethod,
        @Valid TemperatureRange servingTemperature,
        @DecimalMin("0") @DecimalMax("100") BigDecimal strength,
        @Size(max = 30) List<@NotNull @Valid GrapeShare> grapeComposition,
        Boolean grapeCompositionComplete,
        @Valid SensoryProfile sensoryProfile,
        @Size(max = 40) List<@NotNull @Valid Reference> styleTags,
        @Valid CellaringPotential cellaringPotential,
        Aeration aerationRecommendation) {
        @JsonIgnore @AssertTrue(message = "Проверьте доли и повторения сортов винограда")
        public boolean isCompositionValid() { return compositionValid(grapeComposition, grapeCompositionComplete); }
    }

    public record SparklingAttributes(
        @Valid Reference region,
        @Valid Reference appellation,
        @Size(max = 40) List<@NotNull @Valid Reference> aromaTags,
        @Size(max = 40) List<@NotNull @Valid Reference> flavorTags,
        @Size(max = 40) List<@NotNull @Valid Reference> foodPairingTags,
        @Valid Aging aging,
        @Size(max = 4000) String productionMethod,
        @Valid TemperatureRange servingTemperature,
        @DecimalMin("0") @DecimalMax("100") BigDecimal strength,
        @Size(max = 30) List<@NotNull @Valid GrapeShare> grapeComposition,
        Boolean grapeCompositionComplete,
        @Valid SensoryProfile sensoryProfile,
        @Size(max = 40) List<@NotNull @Valid Reference> styleTags,
        @Valid CellaringPotential cellaringPotential,
        Aeration aerationRecommendation,
        @Min(1900) @Max(2100) Integer productionYear,
        VintageStatus vintageStatus,
        @Valid Reference sparklingMethod) {
        @JsonIgnore @AssertTrue(message = "Проверьте доли и повторения сортов винограда")
        public boolean isCompositionValid() { return compositionValid(grapeComposition, grapeCompositionComplete); }
        @JsonIgnore @AssertTrue(message = "Год урожая и статус винтажа должны соответствовать друг другу")
        public boolean isVintageValid() {
            return productionYear == null ? vintageStatus != VintageStatus.vintage : vintageStatus == VintageStatus.vintage;
        }
    }

    public record SpiritAttributes(
        @Valid Reference region,
        @Valid Reference appellation,
        @Size(max = 40) List<@NotNull @Valid Reference> aromaTags,
        @Size(max = 40) List<@NotNull @Valid Reference> flavorTags,
        @Size(max = 40) List<@NotNull @Valid Reference> foodPairingTags,
        @Valid Aging aging,
        @Size(max = 4000) String productionMethod,
        @Valid TemperatureRange servingTemperature,
        @Size(max = 20) List<@NotNull @Valid AgingStage> agingStages,
        @Size(max = 30) List<@NotNull @Valid Reference> servingTags,
        @Size(max = 20) List<@NotNull @Valid SensoryDimension> sensoryRatings,
        @Valid WhiskySpecific whiskyDetails,
        @Valid CognacSpecific cognacDetails) {
        @JsonIgnore @AssertTrue(message = "Нельзя одновременно задавать характеристики виски и коньяка")
        public boolean isSubtypeValid() { return whiskyDetails == null || cognacDetails == null; }
        @JsonIgnore @AssertTrue(message = "Измерения вкуса и номера стадий выдержки не должны повторяться")
        public boolean isUniqueValid() {
            if (agingStages != null && agingStages.stream().filter(java.util.Objects::nonNull).map(AgingStage::order).distinct().count() != agingStages.stream().filter(java.util.Objects::nonNull).count()) return false;
            if (sensoryRatings != null) {
                var codes = new java.util.HashSet<String>();
                for (var entry : sensoryRatings) if (entry != null && entry.dimension() != null && !codes.add(entry.dimension().code())) return false;
            }
            return true;
        }
    }
}
