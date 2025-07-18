package com.innovativesoftware.domsommelier_backend.filter_management.service;

import com.innovativesoftware.domsommelier_backend.filter_management.entity.MultiSelectFilter;
import com.innovativesoftware.domsommelier_backend.filter_management.util.TransliterationUtils;

public class FilterOptionFactory {

    private FilterOptionFactory() {}

    public static MultiSelectFilter.Option createOption(String label) {
        String value = TransliterationUtils.toFilterValue(label);
        return new MultiSelectFilter.Option(value, label);
    }
}