package com.innovativesoftware.domsommelier_backend.annotations;

import com.innovativesoftware.domsommelier_backend.validators.EventTypeSubsetValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventTypeSubsetValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventTypeSubset {
    String message() default "Недопустимый тип мероприятия";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
