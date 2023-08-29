package ru.practicum.shareit.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BookingDatesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStartEndDate {

    String startDateFieldName() default "start";

    String endDateFieldName() default "end";

    String message() default "Дата окончания бронирования не может быть раньше даты начала";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
