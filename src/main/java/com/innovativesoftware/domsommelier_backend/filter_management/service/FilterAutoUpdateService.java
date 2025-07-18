package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.MultiSelectFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.MultiSelectFilterRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.ProductRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterAutoUpdateService {

    private final MultiSelectFilterRepository multiSelectFilterRepo;
    private final FilterRepository filterRepo;
    private final WineRepository wineRepo;
    private final ProductRepository productRepo;

    // Карта field -> supplier уникальных label-значений (человеческих, не value!)
    private final Map<String, Supplier<Set<String>>> fieldLabelExtractors = Map.of(
            "color", this::findDistinctWineColors,
            "type", this::findDistinctWineTypes,
            "grape", this::findDistinctWineGrapes,
            "feature", this::findDistinctWineFeatures,
            "country_name", this::findDistinctProductCountries,
            "producer", this::findDistinctWineProducers,
            "volume", this::findDistinctWineVolumes
    );

    private Set<String> findDistinctWineColors() { return wineRepo.findDistinctColors(); }
    private Set<String> findDistinctWineTypes() { return wineRepo.findDistinctTypes(); }
    private Set<String> findDistinctWineGrapes() { return wineRepo.findDistinctGrapes(); }
    private Set<String> findDistinctWineFeatures() { return wineRepo.findDistinctFeatures(); }
    private Set<String> findDistinctProductCountries() { return productRepo.findDistinctCountries(); }
    private Set<String> findDistinctWineProducers() { return wineRepo.findDistinctProducers(); }
    private Set<String> findDistinctWineVolumes() { return wineRepo.findDistinctVolumes(); }

    @Scheduled(cron = "10 * * * * *") // каждый час @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void updateAllMultiSelectFilters() {
        log.info("Старт автообновления фильтров multi_select...");
        List<MultiSelectFilter> allFilters = multiSelectFilterRepo.findAll();
        for (MultiSelectFilter filter : allFilters) {
            String field = filter.getFilter().getField().toLowerCase();
            Supplier<Set<String>> labelProvider = fieldLabelExtractors.get(field);
            if (labelProvider == null) {
                log.warn("Нет провайдера label для фильтра field={}", field);
                continue;
            }
            Set<String> labels = labelProvider.get();
            if (labels == null) labels = Set.of();

            // Сгенерить новые опции (value + label)
            List<MultiSelectFilter.Option> newOptions = labels.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(FilterOptionFactory::createOption)
                    .distinct()
                    .sorted(Comparator.comparing(MultiSelectFilter.Option::getLabel, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());

            List<MultiSelectFilter.Option> oldOptions = filter.getOptions() != null ? filter.getOptions() : List.of();

            // Сравнить со старыми и обновить если надо
            if (!optionsEqual(oldOptions, newOptions)) {
                filter.setOptions(newOptions);
                multiSelectFilterRepo.save(filter);
                log.info("Обновлён фильтр [{}]: теперь options: {}", filter.getFilter().getName(), newOptions);
            }
        }
    }

    private boolean optionsEqual(List<MultiSelectFilter.Option> a, List<MultiSelectFilter.Option> b) {
        if (a == null) return b == null;
        if (b == null) return false;
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            MultiSelectFilter.Option o1 = a.get(i);
            MultiSelectFilter.Option o2 = b.get(i);
            if (!Objects.equals(o1.getValue(), o2.getValue())) return false;
            if (!Objects.equals(o1.getLabel(), o2.getLabel())) return false;
        }
        return true;
    }
}
