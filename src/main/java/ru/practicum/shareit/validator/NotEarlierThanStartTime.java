package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
@Constraint(validatedBy = BookingEndTimeValidator.class)
@Documented
public @interface NotEarlierThanStartTime {

    String startTimeFieldName() default "start";

    String message() default "Неверная дата окончания бронирования";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

