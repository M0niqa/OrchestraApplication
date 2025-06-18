package com.monika.worek.orchestra.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = StartDateBeforeEndDateValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StartDateBeforeEndDate {
    String message() default "Start date must be before or on the same day as the end date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
