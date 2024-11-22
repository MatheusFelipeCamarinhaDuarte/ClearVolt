package com.fiap.m4g.clearvolt.infra.exception;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MaiorDeIdadeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaiorDeIdade {
    String message() default "A pessoa deve ser maior de 18 anos.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

