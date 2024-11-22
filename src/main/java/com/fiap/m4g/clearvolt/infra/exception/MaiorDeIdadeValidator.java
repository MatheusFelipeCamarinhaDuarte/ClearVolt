package com.fiap.m4g.clearvolt.infra.exception;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class MaiorDeIdadeValidator implements ConstraintValidator<MaiorDeIdade, LocalDate> {

    @Override
    public boolean isValid(LocalDate dataDeNascimento, ConstraintValidatorContext context) {
        if (dataDeNascimento == null) {
            return false; // Se a data de nascimento não for fornecida
        }
        return Period.between(dataDeNascimento, LocalDate.now()).getYears() >= 18;
    }
}
