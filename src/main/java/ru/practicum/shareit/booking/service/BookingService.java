package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.enums.State;

import java.util.List;

public interface BookingService {

    BookingServerDto addBooking(Long userId, BookingClientDto bookingClientDto);

    BookingServerDto approveBooking(Long ownerId, Integer bookingId, Boolean approved);

    BookingServerDto getBooking(Long userId, Integer bookingId);

    List<BookingServerDto> getUserBookings(Long userId, State state, Integer from, Integer size);

    List<BookingServerDto> getItemBookings(Long userId, State state, Integer from, Integer size);
}
