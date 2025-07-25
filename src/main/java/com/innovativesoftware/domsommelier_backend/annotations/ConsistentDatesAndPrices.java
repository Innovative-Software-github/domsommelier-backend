package com.innovativesoftware.domsommelier_backend.annotations;

import com.innovativesoftware.domsommelier_backend.validators.ConsistentDatesAndPricesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ConsistentDatesAndPricesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConsistentDatesAndPrices {
    String message() default "Логическая ошибка в периодах или ценах";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
