package com.innovativesoftware.domsommelier_backend.validators;

import com.innovativesoftware.domsommelier_backend.annotations.EventTypeSubset;
import com.innovativesoftware.domsommelier_backend.event_management.event.enums.EventType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventTypeSubsetValidator implements ConstraintValidator<EventTypeSubset, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return EventType.fromString(value).isPresent();
    }
}
