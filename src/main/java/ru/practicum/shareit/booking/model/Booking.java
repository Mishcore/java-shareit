package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.validator.NotEarlierThanStartTime;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class Booking {
    private Integer id;

    @NotNull
    private LocalDateTime start;

    @NotEarlierThanStartTime
    private LocalDateTime end;

    @NotNull
    private Item item;

    @NotNull
    private Long booker;

    @NotNull
    private Status status;
}