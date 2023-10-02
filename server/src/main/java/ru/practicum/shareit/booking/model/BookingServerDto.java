package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingServerDto {
    private Integer id;
    private UserDto booker;
    private ItemServerDto item;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
}
