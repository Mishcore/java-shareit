package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.enums.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingItemDto {
    private Integer id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
