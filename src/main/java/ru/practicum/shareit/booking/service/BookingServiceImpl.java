package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.InvalidOperationException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.EntityFinder.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final UserRepository userRepo;
    private final ItemRepository itemRepo;

    @Override
    public BookingServerDto addBooking(Long userId, BookingClientDto bookingClientDto) {
        User user = findUserOrThrowException(userRepo, userId);
        Item item = findItemOrThrowException(itemRepo, bookingClientDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Владелец вещи не может забронировать собственную вещь");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException("Вещь недоступна для бронирования");
        }
        Booking booking = bookingRepo.save(BookingMapper.toBooking(item, user, bookingClientDto));
        log.info("Добавлено бронирование вещи ID " + bookingClientDto.getItemId() +
                " от пользователя ID " + userId);
        return BookingMapper.toBookingServerDto(booking);
    }

    @Override
    public BookingServerDto approveBooking(Long ownerId, Integer bookingId, Boolean approved) {
        findUserOrThrowException(userRepo, ownerId);
        Booking booking = findBookingOrThrowException(bookingRepo, bookingId);

        if (booking.getStatus() != Status.WAITING) {
            throw new InvalidOperationException("Нельзя изменить статус бронирования повторно");
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("Бронирование может подтвердить или отклонить только владелец вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            log.info("Бронирование подтверждено владельцем");
        } else {
            booking.setStatus(Status.REJECTED);
            log.info("Бронирование отклонено владельцем");
        }
        return BookingMapper.toBookingServerDto(bookingRepo.save(booking));
    }

    @Transactional(readOnly = true)
    @Override
    public BookingServerDto getBooking(Long userId, Integer bookingId) {
        findUserOrThrowException(userRepo, userId);
        Booking booking = findBookingOrThrowException(bookingRepo, bookingId);

        if (!(userId.equals(booking.getBooker().getId()) ||
                userId.equals(booking.getItem().getOwner().getId()))) {
            throw new UnauthorizedAccessException(
                    "Данные о бронировании может получить только владелец вещи или автор заявки на бронирование"
            );
        }
        log.info("Получены данные о бронировании ID " + bookingId + " для пользователя ID " + userId);
        return BookingMapper.toBookingServerDto(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingServerDto> getUserBookings(Long bookerId, State state, Integer from, Integer size) {
        findUserOrThrowException(userRepo, bookerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepo.findAllByBookerId(bookerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByBookerIdAndStartBeforeAndEndAfter(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case PAST:
                bookings = bookingRepo.findAllByBookerIdAndEndBefore(
                        bookerId, LocalDateTime.now(), pageable
                );
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByBookerIdAndStartAfter(
                        bookerId, LocalDateTime.now(), pageable
                );
                break;
            default:
                bookings = bookingRepo.findAllByBookerIdAndStatus(bookerId, Status.valueOf(state.toString()), pageable);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingServerDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingServerDto> getItemBookings(Long ownerId, State state, Integer from, Integer size) {
        findUserOrThrowException(userRepo, ownerId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());

        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepo.findAllByItemOwnerId(ownerId, pageable);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartBeforeAndEndAfter(
                        ownerId, LocalDateTime.now(), LocalDateTime.now(), pageable
                );
                break;
            case PAST:
                bookings = bookingRepo.findAllByItemOwnerIdAndEndBefore(
                        ownerId, LocalDateTime.now(), pageable
                );
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartAfter(
                        ownerId, LocalDateTime.now(), pageable
                );
                break;
            default:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatus(
                        ownerId, Status.valueOf(state.toString()), pageable
                );
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingServerDto).collect(Collectors.toList());
    }
}
