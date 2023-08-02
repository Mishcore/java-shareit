package ru.practicum.shareit.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class BookingEndTimeValidator implements ConstraintValidator<NotEarlierThanStartTime, LocalDateTime> {

    private String startTimeFieldName;

    @Override
    public void initialize(NotEarlierThanStartTime constraintAnnotation) {
        startTimeFieldName = constraintAnnotation.startTimeFieldName();
    }

    @Override
    public boolean isValid(LocalDateTime endDateTime, ConstraintValidatorContext context) {
        if (endDateTime == null) {
            return true;
        }

        try {
            Field startTimeField = endDateTime.getClass().getDeclaredField(startTimeFieldName);
            startTimeField.setAccessible(true);
            LocalDateTime startDateTime = (LocalDateTime) startTimeField.get(endDateTime);

            return startDateTime == null || !endDateTime.isBefore(startDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
