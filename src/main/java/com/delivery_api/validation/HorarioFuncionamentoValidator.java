package com.delivery_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class HorarioFuncionamentoValidator implements ConstraintValidator<ValidHorarioFuncionamento, String> {

    private static final Pattern HORARIO_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || !HORARIO_PATTERN.matcher(value).matches()) {
            return false;
        }
        try {
            String[] partes = value.split("-");
            LocalTime inicio = LocalTime.parse(partes[0]);
            LocalTime fim = LocalTime.parse(partes[1]);
            return inicio.isBefore(fim);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}