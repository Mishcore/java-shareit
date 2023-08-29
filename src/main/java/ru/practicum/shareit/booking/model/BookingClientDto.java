package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validator.ValidStartEndDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@ValidStartEndDate
@AllArgsConstructor
public class BookingClientDto {
    @Positive
    @NotNull
    private Integer itemId;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
}
