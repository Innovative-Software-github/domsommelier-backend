package com.innovativesoftware.domsommelier_backend.product_management.product.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class VolumeStrengthUtils {

    public static BigDecimal parseVolume(String raw) {
        try {
            String cleaned = raw.replace("л", "")
                    .replace("l", "")
                    .replace(",", ".")
                    .replace(" ", "")
                    .trim();
            BigDecimal value = new BigDecimal(cleaned);
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Объём должен быть больше нуля: " + raw);
            }
            return value.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Неправильный формат объёма: " + raw, e);
        }
    }

    public static BigDecimal parseStrength(String raw) {
        try {
            String cleaned = raw.replace("%", "")
                    .replace(",", ".")
                    .replace(" ", "")
                    .trim();
            BigDecimal value = new BigDecimal(cleaned);
            if (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("Крепость должна быть в диапазоне 0-100%: " + raw);
            }
            return value.setScale(1, RoundingMode.HALF_UP);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("Неправильный формат крепости: " + raw, e);
        }
    }

    public static String formatVolume(BigDecimal volume) {
        if (volume == null) return "";
        return volume.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + " л";
    }

    public static String formatStrength(BigDecimal strength) {
        if (strength == null) return "";
        return strength.setScale(1, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() + " %";
    }
}
