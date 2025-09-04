package com.delivery_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HorarioFuncionamentoValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHorarioFuncionamento {
    String message() default "Formato de horário inválido. Use HH:MM-HH:MM e a hora de início deve ser menor que a de fim.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}