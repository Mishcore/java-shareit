package ru.practicum.shareit;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@UtilityClass
public class EntityFinder {
    public static User findUserOrThrowException(UserRepository userRepo, Long userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return userOpt.get();
    }

    public static Item findItemOrThrowException(ItemRepository itemRepo, Integer itemId) {
        Optional<Item> itemOpt = itemRepo.findById(itemId);
        if (itemOpt.isEmpty()) {
            throw new EntityNotFoundException("Вещь не найдена");
        }
        return itemOpt.get();
    }

    public static Booking findBookingOrThrowException(BookingRepository bookingRepo, Integer bookingId) {
        Optional<Booking> bookingOpt = bookingRepo.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new EntityNotFoundException("Бронирование не найдено");
        }
        return bookingOpt.get();
    }

    public static Request findRequestOrThrowException(RequestRepository requestRepo, Integer requestId) {
        Optional<Request> requestOpt = requestRepo.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new EntityNotFoundException("Запрос на вещь не найден");
        }
        return requestOpt.get();
    }
}
