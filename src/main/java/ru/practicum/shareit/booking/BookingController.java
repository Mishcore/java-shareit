package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.State;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingServerDto addBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId, @RequestBody @Validated BookingClientDto bookingClientDto) {
        log.info("Принят запрос на добавление бронирования");
        return bookingService.addBooking(userId, bookingClientDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingServerDto approveBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
            @PathVariable @Positive Integer bookingId,
            @RequestParam @NotNull Boolean approved) {
        log.info("Принят запрос на подтверждение или отклонение бронирования ID " + bookingId);
        return bookingService.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingServerDto getBooking(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId, @PathVariable @Positive Integer bookingId) {
        log.info("Принят запрос на получение данных бронирования ID " + bookingId + " от пользователя ID " + userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingServerDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestParam (defaultValue = "ALL") State state) {
        log.info("Принят запрос на получение списка бронирований пользователя ID " + userId);
        return bookingService.getUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingServerDto> getItemBookings(
            @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
            @RequestParam (defaultValue = "ALL") State state) {
        log.info("Принят запрос на получение списка бронирований для всех вещей пользователя ID " + userId);
        return bookingService.getItemBookings(userId, state);
    }
}
