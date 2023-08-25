package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.enums.State;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidOperationException;
import ru.practicum.shareit.exception.ItemNotAvailableException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        User user = findUserOrThrowException(userId);
        Item item = findItemOrThrowException(bookingClientDto.getItemId());

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
        User owner = findUserOrThrowException(ownerId);
        Booking booking = findBookingOrThrowException(bookingId);

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
        User user = findUserOrThrowException(userId);
        Booking booking = findBookingOrThrowException(bookingId);

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
    public List<BookingServerDto> getUserBookings(Long bookerId, State state) {
        User user = findUserOrThrowException(bookerId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now(), LocalDateTime.now()
                );
                break;
            case PAST:
                bookings = bookingRepo.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        bookerId, LocalDateTime.now()
                );
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        bookerId, LocalDateTime.now()
                );
                break;
            default:
                bookings = bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.valueOf(state.toString()));
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingServerDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingServerDto> getItemBookings(Long ownerId, State state) {
        User user = findUserOrThrowException(ownerId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepo.findAllByItemOwnerIdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now(), LocalDateTime.now()
                );
                break;
            case PAST:
                bookings = bookingRepo.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        ownerId, LocalDateTime.now()
                );
                break;
            case FUTURE:
                bookings = bookingRepo.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        ownerId, LocalDateTime.now()
                );
                break;
            default:
                bookings = bookingRepo.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.valueOf(state.toString()));
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingServerDto).collect(Collectors.toList());
    }

    private Item findItemOrThrowException(Integer itemId) {
        Optional<Item> itemOpt = itemRepo.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new EntityNotFoundException("Вещь не найдена");
        }
        return itemOpt.get();
    }

    private User findUserOrThrowException(Long userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return userOpt.get();
    }

    private Booking findBookingOrThrowException(Integer bookingId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new EntityNotFoundException("Бронирование не найдено");
        }
        return bookingOpt.get();
    }
}
