package com.innovativesoftware.domsommelier_backend.annotations;

import com.innovativesoftware.domsommelier_backend.validators.NotBeforeTodayValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotBeforeTodayValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBeforeToday {
    String message() default "Дата должна быть не раньше сегодняшнего дня";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
