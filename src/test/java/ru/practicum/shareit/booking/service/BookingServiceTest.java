package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private final User user1 = new User(1L, "user1", "test@mail.ru");
    private final User user2 = new User(2L, "user2", "test@mail.com");

    private final Item item1 = new Item(1, "item1", "description1", false, user1, null);
    private final Item item2 = new Item(2, "item2", "description2", true, user2, null);

    private final Booking booking1 = new Booking(
            1, item2, user1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

    @Test
    void addBookingWhenUserAndItemExistAndValidParamsThenPersistBookingAndReturnBookingDto() {
        BookingClientDto bookingDto =
                new BookingClientDto(2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(2)).thenReturn(Optional.of(item2));
        when(bookingRepository.save(any()))
                .thenReturn(
                        new Booking(1, item2, user1, LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(2), Status.WAITING));

        BookingServerDto actualBooking = bookingService.addBooking(1L, bookingDto);

        assertEquals(1, actualBooking.getId());
        assertEquals(item2.getId(), actualBooking.getItem().getId());
        assertEquals(user1.getId(), actualBooking.getBooker().getId());
        assertEquals(Status.WAITING, actualBooking.getStatus());
    }

    @Test
    void addBookingWhenEntityNotFoundThenException() {
        BookingClientDto bookingDto =
                new BookingClientDto(2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        // case: missing user
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception1 = assertThrows(EntityNotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertEquals("Пользователь не найден", exception1.getMessage());

        //case: missing item
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(2)).thenReturn(Optional.empty());

        EntityNotFoundException exception2 = assertThrows(EntityNotFoundException.class,
                () -> bookingService.addBooking(1L, bookingDto));
        assertEquals("Вещь не найдена", exception2.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void addBookingWhenBookedByOwnerThenException() {
        BookingClientDto bookingDto =
                new BookingClientDto(2, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(2)).thenReturn(Optional.of(item2));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> bookingService.addBooking(2L, bookingDto));
        assertEquals("Владелец вещи не может забронировать собственную вещь", exception.getMessage());
    }

    @Test
    void addBookingWhenItemNotAvailableThenException() {
        BookingClientDto bookingDto =
                new BookingClientDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        ItemNotAvailableException exception = assertThrows(ItemNotAvailableException.class,
                () -> bookingService.addBooking(2L, bookingDto));
        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void approveBookingWhenApprovedThenUpdateBookingAndReturnBookingDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenReturn(booking1);

        BookingServerDto actualBooking = bookingService.approveBooking(2L, 1, true);
        assertEquals(Status.APPROVED, actualBooking.getStatus());
    }

    @Test
    void approveBookingWhenRejectedThenUpdateBookingAndReturnBookingDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenReturn(booking1);

        BookingServerDto actualBooking = bookingService.approveBooking(2L, 1, false);
        assertEquals(Status.REJECTED, actualBooking.getStatus());
    }

    @Test
    void approveBookingWhenApprovingOrRejectingAlreadyApprovedBookingThenException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenReturn(booking1);
        bookingService.approveBooking(2L, 1, true);

        // case: approved to approved
        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> bookingService.approveBooking(2L, 1, true));
        assertEquals("Нельзя изменить статус бронирования повторно", exception.getMessage());
        // case: approved to rejected
        exception = assertThrows(InvalidOperationException.class, () -> bookingService.approveBooking(2L, 1, false));
        assertEquals("Нельзя изменить статус бронирования повторно", exception.getMessage());

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void approveBookingWhenApprovingOrRejectingAlreadyRejectedBookingThenException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any())).thenReturn(booking1);
        bookingService.approveBooking(2L, 1, false);

        // case: rejected to approved
        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> bookingService.approveBooking(2L, 1, true));
        assertEquals("Нельзя изменить статус бронирования повторно", exception.getMessage());
        // case: rejected to rejected
        exception = assertThrows(InvalidOperationException.class, () -> bookingService.approveBooking(2L, 1, false));
        assertEquals("Нельзя изменить статус бронирования повторно", exception.getMessage());

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void approveBookingWhenApprovingOrRejectingByOwnerThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking1));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> bookingService.approveBooking(1L, 1, true));
        assertEquals("Бронирование может подтвердить или отклонить только владелец вещи", exception.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingWhenUserIsOwnerOrBookerWhenBookingDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking1));

        BookingServerDto actualBookingByBooker = bookingService.getBooking(1L, 1);
        assertEquals(booking1.getStart(), actualBookingByBooker.getStart());
        assertEquals(booking1.getEnd(), actualBookingByBooker.getEnd());
        assertEquals(booking1.getItem().getName(), actualBookingByBooker.getItem().getName());
        assertEquals(booking1.getBooker().getName(), actualBookingByBooker.getBooker().getName());
        assertEquals(booking1.getStatus(), actualBookingByBooker.getStatus());

        BookingServerDto actualBookingByOwner = bookingService.getBooking(2L, 1);
        assertEquals(booking1.getStart(), actualBookingByOwner.getStart());
        assertEquals(booking1.getEnd(), actualBookingByOwner.getEnd());
        assertEquals(booking1.getItem().getName(), actualBookingByOwner.getItem().getName());
        assertEquals(booking1.getBooker().getName(), actualBookingByOwner.getBooker().getName());
        assertEquals(booking1.getStatus(), actualBookingByOwner.getStatus());
    }

    @Test
    void getBookingWhenUserIsNotOwnerOrBookerThenException() {
        User user3 = new User(3L, "user3", "test@mail.cn");
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));
        when(bookingRepository.findById(1)).thenReturn(Optional.of(booking1));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> bookingService.getBooking(3L, 1));
        assertEquals("Данные о бронировании может получить только владелец вещи или автор заявки на бронирование",
                exception.getMessage());
    }

    @Test
    void getBookingWhenEntityNotFoundThenException() {
        // case: missing user
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception1 = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(1L, 1));
        assertEquals("Пользователь не найден", exception1.getMessage());

        // case: missing booking
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception2 = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getBooking(1L, 1));
        assertEquals("Бронирование не найдено", exception2.getMessage());
    }

    @Test
    void getUserBookingsWhenUserAndBookingsExistThenBookingDtoList() {
        Booking booking2 = new Booking(
                2, item1, user2, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Status.APPROVED);
        Booking booking3 = new Booking(
                3, item1, user2, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), Status.REJECTED);
        Booking booking4 = new Booking(
                4, item1, user2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), Status.WAITING);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        // case: ALL
        when(bookingRepository
                .findAllByBookerId(anyLong(), any()))
                .thenReturn(List.of(booking4, booking2, booking3));
        List<BookingServerDto> actualBookings1 = bookingService.getUserBookings(2L, State.ALL, 0, 1);
        assertEquals(3, actualBookings1.size());
        // case: CURRENT
        when(bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking3));
        List<BookingServerDto> actualBookings2 = bookingService.getUserBookings(2L, State.CURRENT, 0, 1);
        assertEquals(1, actualBookings2.size());
        assertEquals(booking3.getId(), actualBookings2.get(0).getId());
        // case: PAST
        when(bookingRepository
                .findAllByBookerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking2));
        List<BookingServerDto> actualBookings3 = bookingService.getUserBookings(2L, State.PAST, 0, 1);
        assertEquals(1, actualBookings3.size());
        assertEquals(booking2.getId(), actualBookings3.get(0).getId());
        // case: FUTURE
        when(bookingRepository
                .findAllByBookerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking4));
        List<BookingServerDto> actualBookings4 = bookingService.getUserBookings(2L, State.FUTURE, 0, 1);
        assertEquals(1, actualBookings4.size());
        assertEquals(booking4.getId(), actualBookings4.get(0).getId());
        // case: WAITING
        when(bookingRepository
                .findAllByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking4));
        List<BookingServerDto> actualBookings5 = bookingService.getUserBookings(2L, State.WAITING, 0, 1);
        assertEquals(1, actualBookings5.size());
        assertEquals(booking4.getId(), actualBookings5.get(0).getId());
        // case: REJECTED
        when(bookingRepository
                .findAllByBookerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking3));
        List<BookingServerDto> actualBookings6 = bookingService.getUserBookings(2L, State.REJECTED, 0, 1);
        assertEquals(1, actualBookings6.size());
        assertEquals(booking3.getId(), actualBookings6.get(0).getId());
    }

    @Test
    void getUserBookingsWhenUserNotFoundThenException() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getUserBookings(3L, State.ALL, 0, 1));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getItemBookingsWhenUserAndBookingsExistThenBookingDtoList() {
        Booking booking2 = new Booking(
                2, item1, user2, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Status.APPROVED);
        Booking booking3 = new Booking(
                3, item1, user2, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), Status.REJECTED);
        Booking booking4 = new Booking(
                4, item1, user2, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), Status.WAITING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        // case: ALL
        when(bookingRepository
                .findAllByItemOwnerId(anyLong(), any()))
                .thenReturn(List.of(booking4, booking2, booking3));
        List<BookingServerDto> actualBookings1 = bookingService.getItemBookings(1L, State.ALL, 0, 1);
        assertEquals(3, actualBookings1.size());
        // case: CURRENT
        when(bookingRepository
                .findAllByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking3));
        List<BookingServerDto> actualBookings2 = bookingService.getItemBookings(1L, State.CURRENT, 0, 1);
        assertEquals(1, actualBookings2.size());
        assertEquals(booking3.getId(), actualBookings2.get(0).getId());
        // case: PAST
        when(bookingRepository
                .findAllByItemOwnerIdAndEndBefore(anyLong(), any(), any()))
                .thenReturn(List.of(booking2));
        List<BookingServerDto> actualBookings3 = bookingService.getItemBookings(1L, State.PAST, 0, 1);
        assertEquals(1, actualBookings3.size());
        assertEquals(booking2.getId(), actualBookings3.get(0).getId());
        // case: FUTURE
        when(bookingRepository
                .findAllByItemOwnerIdAndStartAfter(anyLong(), any(), any()))
                .thenReturn(List.of(booking4));
        List<BookingServerDto> actualBookings4 = bookingService.getItemBookings(1L, State.FUTURE, 0, 1);
        assertEquals(1, actualBookings4.size());
        assertEquals(booking4.getId(), actualBookings4.get(0).getId());
        // case: WAITING
        when(bookingRepository
                .findAllByItemOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking4));
        List<BookingServerDto> actualBookings5 = bookingService.getItemBookings(1L, State.WAITING, 0, 1);
        assertEquals(1, actualBookings5.size());
        assertEquals(booking4.getId(), actualBookings5.get(0).getId());
        // case: REJECTED
        when(bookingRepository
                .findAllByItemOwnerIdAndStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking3));
        List<BookingServerDto> actualBookings6 = bookingService.getItemBookings(1L, State.REJECTED, 0, 1);
        assertEquals(1, actualBookings6.size());
        assertEquals(booking3.getId(), actualBookings6.get(0).getId());
    }

    @Test
    void getItemBookingsWhenUserNotFoundThenException() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> bookingService.getItemBookings(3L, State.ALL, 0, 1));
        assertEquals("Пользователь не найден", exception.getMessage());
    }
}