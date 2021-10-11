package com.vari.clockify.check.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { NeededTagValidator.class })
public @interface NeededTag {
    String message() default "Missing required tag";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
