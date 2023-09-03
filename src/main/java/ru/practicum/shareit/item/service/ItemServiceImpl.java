package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingItemDto;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.InvalidOperationException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.dao.CommentRepository;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.EntityFinder.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final BookingRepository bookingRepo;
    private final CommentRepository commentRepo;
    private final RequestRepository requestRepo;

    @Transactional(readOnly = true)
    @Override
    public List<ItemServerDto> getAllUserItems(Long ownerId, Integer from, Integer size) {
        User owner = findUserOrThrowException(userRepo, ownerId);
        List<Item> userItems = itemRepo.findAllByOwnerId(
                ownerId,
                PageRequest.of(from / size, size));
        log.info("Получен список всех вещей пользователя ID " + ownerId);
        return userItems.stream()
                .map(ItemMapper::toItemServerDto)
                .map(this::setLastAndNextBookings)
                .map(this::setComments)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemServerDto getItem(Long userId, Integer itemId) {
        Item item = findItemOrThrowException(itemRepo, itemId);
        log.info("Получена вещь ID " + itemId);
        ItemServerDto itemServerDto = ItemMapper.toItemServerDto(item);
        if (userId.equals(item.getOwner().getId())) {
            log.info("Запрос принят от владельца вещи. Будет добавлена информация о бронированиях");
            setLastAndNextBookings(itemServerDto);
        }
        setComments(itemServerDto);
        return itemServerDto;
    }

    @Override
    public ItemServerDto addItem(Long ownerId, ItemClientDto itemDto) {
        User owner = findUserOrThrowException(userRepo, ownerId);
        Request request = null;
        if (itemDto.getRequestId() != null) {
            request = findRequestOrThrowException(requestRepo, itemDto.getRequestId());
        }
        Item item = itemRepo.save(ItemMapper.toItem(owner, request, itemDto));

        StringBuilder logBuilder = new StringBuilder(
                "Добавлена новая вещь ID " + item.getId() + " пользователя ID " + ownerId);
        if (request != null) {
            logBuilder.append(
                    " по запросу ID " + request.getId() + " от пользователя ID" + request.getRequestor().getId());
        }
        log.info(logBuilder.toString());

        return ItemMapper.toItemServerDto(item);
    }

    @Override
    public ItemServerDto editItem(Long ownerId, Integer itemId, ItemClientDto itemDto) {
        findUserOrThrowException(userRepo, ownerId);
        Item itemFromRepo = findItemOrThrowException(itemRepo, itemId);

        if (!ownerId.equals(itemFromRepo.getOwner().getId())) {
            throw new UnauthorizedAccessException("Пользователь не является владельцем вещи");
        }
        if (itemDto.getName() != null) {
            itemFromRepo.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemFromRepo.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemFromRepo.setAvailable(itemDto.getAvailable());
        }
        validateItem(itemFromRepo);
        Item item = itemRepo.save(itemFromRepo);
        log.info("Отредактированы данные вещи ID " + itemId + " пользователя ID " + ownerId);
        return ItemMapper.toItemServerDto(item);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemServerDto> getItemsBySearch(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            log.info("Строка поиска пуста. Возвращён пустой список");
            return Collections.emptyList();
        }
        List<Item> items = itemRepo.search(text, PageRequest.of(from / size, size));
        log.info("Получен список вещей по поисковой строке \"" + text + "\"");
        return items.stream().map(ItemMapper::toItemServerDto).collect(Collectors.toList());
    }

    @Override
    public CommentServerDto addComment(Long authorId, Integer itemId, CommentClientDto commentDto) {
        User user = findUserOrThrowException(userRepo, authorId);
        Item item = findItemOrThrowException(itemRepo, itemId);

        if (item.getOwner().getId().equals(authorId)) {
            throw new InvalidOperationException("Владелец не может оставлять комментарии к собственной вещи");
        }
        List<Booking> bookingOpt = bookingRepo.findAllByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, authorId, Status.APPROVED, LocalDateTime.now());
        if (bookingOpt.isEmpty()) {
            throw new InvalidOperationException(
                    "Пользователь, не бравший вещь в аренду, не может оставлять комментарии к ней");
        }
        Comment comment = commentRepo.save(CommentMapper.toComment(user, item, commentDto));
        log.info("Добавлен комментарий к вещи ID " + itemId + " от пользователя ID " + authorId);
        return CommentMapper.toCommentServerDto(comment);
    }

    private ItemServerDto setLastAndNextBookings(ItemServerDto itemServerDto) {
        List<Booking> bookings = bookingRepo.findAllByItemId(itemServerDto.getId());
        Optional<BookingItemDto> lastBookingOpt = bookings.stream()
                .filter(booking -> booking.getStatus() != Status.REJECTED)
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingItemDto);
        Optional<BookingItemDto> nextBookingOpt = bookings.stream()
                .filter(booking -> booking.getStatus() != Status.REJECTED)
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStart))
                .map(BookingMapper::toBookingItemDto);
        lastBookingOpt.ifPresent(itemServerDto::setLastBooking);
        nextBookingOpt.ifPresent(itemServerDto::setNextBooking);
        return itemServerDto;
    }

    private ItemServerDto setComments(ItemServerDto itemServerDto) {
        List<CommentServerDto> comments = commentRepo.findAllByItemId(itemServerDto.getId()).stream()
                .map(CommentMapper::toCommentServerDto)
                .collect(Collectors.toList());
        itemServerDto.setComments(comments);
        return itemServerDto;
    }

    private void validateItem(Item item) {
        Set<ConstraintViolation<Item>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(item);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}