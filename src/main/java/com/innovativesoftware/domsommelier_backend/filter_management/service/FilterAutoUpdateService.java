package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.Filter;
import com.innovativesoftware.domsommelier_backend.filter_management.entity.MultiSelectFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.enums.FilterType;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.FilterRepository;
import com.innovativesoftware.domsommelier_backend.filter_management.repository.MultiSelectFilterRepository;
import com.innovativesoftware.domsommelier_backend.product_management.product.enums.ProductCategoryEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilterAutoUpdateService {

    private final MultiSelectFilterRepository multiSelectFilterRepo;
    private final FilterRepository filterRepo;
    private final List<ProductFilterFieldProvider> providers;

    @Scheduled(cron = "5 * * * * *")
    @Transactional
    public void updateAllMultiSelectFilters() {
        log.info("Старт автообновления фильтров multi_select...");

        for (ProductFilterFieldProvider provider : providers) {
            log.info("Обновление фильтров для продукта [{}]", provider.getSupportedCategory().name());
            ProductCategoryEnum categoryEnum = provider.getSupportedCategory();
            Map<String, String> fieldRuNames = provider.getFieldRuNames();

            List<String> supportedFields = new ArrayList<>(fieldRuNames.keySet());

            Map<String, Filter> filterEntities = filterRepo.findByProductCategories(categoryEnum)
                    .stream()
                    .filter(f -> f.getType() == FilterType.multi_select)
                    .collect(Collectors.toMap(f -> f.getField().toLowerCase(), f -> f));

            for (String field : supportedFields) {
                Filter filterEntity = filterEntities.get(field);
                if (filterEntity == null) {
                    continue;
                }

                Set<String> labels = provider.getLabels(field);
                if (labels == null) labels = Set.of();

                List<MultiSelectFilter.Option> newOptions = labels.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(FilterOptionFactory::createOption)
                        .distinct()
                        .sorted(Comparator.comparing(MultiSelectFilter.Option::getLabel, String.CASE_INSENSITIVE_ORDER))
                        .collect(Collectors.toList());

                MultiSelectFilter msf = multiSelectFilterRepo.findById(filterEntity.getId()).orElse(null);
                if (msf == null) {
                    msf = new MultiSelectFilter();
                    msf.setId(filterEntity.getId());
                    msf.setFilter(filterEntity);
                    msf.setOptions(new ArrayList<>(newOptions));
                    multiSelectFilterRepo.save(msf);
                    log.info("Создан новый MultiSelectFilter [{}] с options: {}", filterEntity.getName(), newOptions);
                } else {
                    List<MultiSelectFilter.Option> oldOptions = msf.getOptions() != null ? msf.getOptions() : List.of();
                    if (!optionsEqual(oldOptions, newOptions)) {
                        msf.setOptions(new ArrayList<>(newOptions));
                        multiSelectFilterRepo.save(msf);
                        log.info("Обновлён фильтр [{}] для продукта [{}]: options: {}", filterEntity.getName(),
                                filterEntity.getProductCategoryEnum().name(), newOptions);
                    }
                }
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
            if (!Objects.equals(o1.getVal(), o2.getVal())) return false;
            if (!Objects.equals(o1.getLabel(), o2.getLabel())) return false;
        }
        return true;
    }
}
