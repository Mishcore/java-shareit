package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemServerDto {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentServerDto> comments;
}
