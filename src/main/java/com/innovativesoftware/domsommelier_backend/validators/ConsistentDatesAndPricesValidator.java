package com.innovativesoftware.domsommelier_backend.validators;

import com.innovativesoftware.domsommelier_backend.annotations.ConsistentDatesAndPrices;
import com.innovativesoftware.domsommelier_backend.event_management.event.model.EventFilterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ConsistentDatesAndPricesValidator implements ConstraintValidator<ConsistentDatesAndPrices, EventFilterRequest> {

    @Override
    public boolean isValid(EventFilterRequest req, ConstraintValidatorContext context) {
        boolean valid = true;

        // dateEnd >= dateStart
        if (req.getDateStart() != null && req.getDateEnd() != null) {
            if (req.getDateEnd().isBefore(req.getDateStart())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Дата конца не может быть раньше даты начала")
                        .addPropertyNode("dateEnd")
                        .addConstraintViolation();
                valid = false;
            }
        }

        // priceMin <= priceMax
        if (req.getPriceMin() != null && req.getPriceMax() != null) {
            if (req.getPriceMin() > req.getPriceMax()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Минимальная цена не может быть больше максимальной")
                        .addPropertyNode("priceMin")
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
