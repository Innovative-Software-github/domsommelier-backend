package com.innovativesoftware.domsommelier_backend.product_management.store.utils;

import com.innovativesoftware.domsommelier_backend.product_management.store.entity.WineStorePoint;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PointConverter implements AttributeConverter<WineStorePoint, String> {

    @Override
    public String convertToDatabaseColumn(WineStorePoint point) {
        if (point == null) {
            return null;
        }
        return String.format("(%f,%f)", point.getLongitude(), point.getLatitude());
    }

    @Override
    public WineStorePoint convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }

        String cleaned = dbData.replaceAll("[()]", "");
        String[] parts = cleaned.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid point format: " + dbData);
        }

        try {
            Double longitude = Double.parseDouble(parts[0].trim());
            Double latitude = Double.parseDouble(parts[1].trim());
            return new WineStorePoint(longitude, latitude);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid point coordinates: " + dbData, e);
        }
    }
}