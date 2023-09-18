package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStateException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> addBooking(@RequestHeader(USER_ID) long userId,
											 @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Запрос на создание бронирования {}, userId={}", requestDto, userId);
		return bookingClient.addBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID) long userId,
												 @PathVariable int bookingId,
												 @RequestParam boolean approved) {
		log.info("Запрос на подтверждение/отклонение бронирования Id={}, userId={}", bookingId, userId);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
											 @PathVariable int bookingId) {
		log.info("Запрос на получение бронирования {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader(USER_ID) long userId,
												  @RequestParam(name = "state", defaultValue = "all") String stateParam,
												  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
												  @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		log.info("Запрос на получение бронирований со статусом {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getUserBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getItemBookings(@RequestHeader(USER_ID) long userId,
												  @RequestParam(name = "state", defaultValue = "all") String stateParam,
												  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
												  @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new UnsupportedStateException("Unknown state: " + stateParam));
		log.info("Запрос на получение бронирований со статусом {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getItemBookings(userId, state, from, size);
	}
}
