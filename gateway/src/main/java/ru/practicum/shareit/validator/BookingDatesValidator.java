package ru.practicum.shareit.validator;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookingDatesValidator implements ConstraintValidator<ValidStartEndDate, Object> {
    private String startDateFieldName;
    private String endDateFieldName;

    @Override
    public void initialize(ValidStartEndDate annotation) {
        this.startDateFieldName = annotation.startDateFieldName();
        this.endDateFieldName = annotation.endDateFieldName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        LocalDateTime startDate = (LocalDateTime) new BeanWrapperImpl(value).getPropertyValue(startDateFieldName);
        LocalDateTime endDate = (LocalDateTime) new BeanWrapperImpl(value).getPropertyValue(endDateFieldName);
        if (startDate == null || endDate == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата начала или окончания бронирования не может быть null")
                    .addConstraintViolation();
            return false;
        } else if (startDate.isBefore(LocalDateTime.now()) || endDate.isBefore(LocalDateTime.now())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Дата начала или окончания бронирования не может быть раньше настоящего времени"
                    ).addConstraintViolation();
            return false;
        } else {
            return endDate.isAfter(startDate);
        }
    }
}
