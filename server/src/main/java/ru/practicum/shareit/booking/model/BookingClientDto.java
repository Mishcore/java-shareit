package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingClientDto {
    private Integer itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
