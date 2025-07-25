package com.innovativesoftware.domsommelier_backend.validators;

import com.innovativesoftware.domsommelier_backend.annotations.NotBeforeToday;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.OffsetDateTime;

public class NotBeforeTodayValidator implements ConstraintValidator<NotBeforeToday, OffsetDateTime> {

    @Override
    public boolean isValid(OffsetDateTime value, ConstraintValidatorContext context) {
        if (value == null) return true;
        OffsetDateTime now = OffsetDateTime.now();
        return !value.toLocalDate().isBefore(now.toLocalDate());
    }
}
