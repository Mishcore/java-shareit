package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingServerDto addBooking(
            @RequestHeader(USER_ID) @Positive Long userId, @RequestBody @Valid BookingClientDto bookingClientDto) {
        log.info("Принят запрос на добавление бронирования");
        return bookingService.addBooking(userId, bookingClientDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingServerDto approveBooking(
            @RequestHeader(USER_ID) @Positive Long ownerId,
            @PathVariable @Positive Integer bookingId,
            @RequestParam @NotNull Boolean approved) {
        log.info("Принят запрос на подтверждение или отклонение бронирования ID " + bookingId);
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingServerDto getBooking(
            @RequestHeader(USER_ID) @Positive Long userId, @PathVariable @Positive Integer bookingId) {
        log.info("Принят запрос на получение данных бронирования ID " + bookingId + " от пользователя ID " + userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingServerDto> getUserBookings(
            @RequestHeader(USER_ID) @Positive Long userId,
            @RequestParam (defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Принят запрос на получение списка бронирований пользователя ID " + userId);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingServerDto> getItemBookings(
            @RequestHeader(USER_ID) @Positive Long userId,
            @RequestParam (defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Принят запрос на получение списка бронирований для всех вещей пользователя ID " + userId);
        return bookingService.getItemBookings(userId, state, from, size);
    }
}
