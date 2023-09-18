package ru.practicum.shareit.booking;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public static Booking toBooking(Item item, User booker, BookingClientDto bookingClientDto) {
        return new Booking(
                null,
                item,
                booker,
                bookingClientDto.getStart(),
                bookingClientDto.getEnd(),
                Status.WAITING
        );
    }

    public static BookingServerDto toBookingServerDto(Booking booking) {
        return new BookingServerDto(
                booking.getId(),
                UserMapper.toUserDto(booking.getBooker()),
                ItemMapper.toItemServerDto(booking.getItem()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static BookingItemDto toBookingItemDto(Booking booking) {
        return new BookingItemDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }
}
