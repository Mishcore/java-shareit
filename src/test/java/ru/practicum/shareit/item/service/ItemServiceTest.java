package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.InvalidOperationException;
import ru.practicum.shareit.exception.UnauthorizedAccessException;
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

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    private final User user1 = new User(1L, "test user 1", "test@mail.ru");
    private final User user2 = new User(2L, "test user 2", "test@mail.com");

    private final Request request1 = new Request(1, "test request 1", user1, LocalDateTime.of(2000, 1, 1, 0, 0));

    private final Item item1 = new Item(1, "test item 1", "test description 1", true, user1, null);
    private final Item item2 = new Item(2, "test item 2", "test description 2", true, user1, null);
    private final Item item3 = new Item(3, "test item 3", "test description 3", true, user2, null);
    private final Item item4 = new Item(4, "test item 4", "test description 4", false, user2, null);

    private final Booking booking1 = new Booking(
            1, item1, user2, LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.of(2000, 1, 2, 0, 0), Status.APPROVED);
    private final Booking booking2 = new Booking(
            2, item1, user2, LocalDateTime.of(2666, 1, 1, 0, 0), LocalDateTime.of(2666, 1, 2, 0, 0), Status.APPROVED);
    private final Comment comment1 = new Comment(1, "very good", item1, user2, LocalDateTime.now());

    @Test
    void getAllUserItemsWhenItemsExistThenItemDtoList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L, Pageable.ofSize(1))).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemId(1)).thenReturn(List.of(booking1, booking2));
        when(commentRepository.findAllByItemId(1)).thenReturn(List.of(comment1));
        List<ItemServerDto> actualItems = itemService.getAllUserItems(1L, 0, 1);

        assertEquals(2, actualItems.size());

        assertEquals(item1.getId(), actualItems.get(0).getId());
        assertEquals(item1.getName(), actualItems.get(0).getName());
        assertEquals(item1.getDescription(), actualItems.get(0).getDescription());
        assertEquals(item1.getAvailable(), actualItems.get(0).getAvailable());
        assertNotNull(actualItems.get(0).getLastBooking());
        assertNotNull(actualItems.get(0).getNextBooking());
        assertEquals(1, actualItems.get(0).getComments().size());

        assertEquals(item2.getId(), actualItems.get(1).getId());
        assertEquals(item2.getName(), actualItems.get(1).getName());
        assertEquals(item2.getDescription(), actualItems.get(1).getDescription());
        assertEquals(item2.getAvailable(), actualItems.get(1).getAvailable());
        assertNull(actualItems.get(1).getLastBooking());
        assertNull(actualItems.get(1).getNextBooking());
        assertEquals(0, actualItems.get(1).getComments().size());
    }

    @Test
    void getAllUserItemsWhenNoItemExistsThenEmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L, Pageable.ofSize(1))).thenReturn(Collections.emptyList());
        List<ItemServerDto> actualItems = assertDoesNotThrow(() -> itemService.getAllUserItems(1L, 0, 1));
        assertTrue(actualItems.isEmpty());
    }

    @Test
    void getAllUserItemsWhenUserNotFoundThenException() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.getAllUserItems(3L, 0, 1));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(itemRepository, never()).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getItemWhenRequestedByOwnerThenItemDtoWithBookingsAndComments() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByItemId(1)).thenReturn(List.of(booking1, booking2));
        when(commentRepository.findAllByItemId(1)).thenReturn(List.of(comment1));
        ItemServerDto actualItem = itemService.getItem(1L, 1);

        assertEquals(item1.getId(), actualItem.getId());
        assertEquals(item1.getName(), actualItem.getName());
        assertEquals(item1.getDescription(), actualItem.getDescription());
        assertEquals(item1.getAvailable(), actualItem.getAvailable());
        assertNotNull(actualItem.getLastBooking());
        assertNotNull(actualItem.getNextBooking());
        assertEquals(1, actualItem.getComments().size());
    }

    @Test
    void getItemWhenRequestedByOtherUserThenItemDtoWithoutBookingsAndWithComments() {
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1)).thenReturn(List.of(comment1));
        ItemServerDto actualItem = itemService.getItem(2L, 1);

        assertEquals(item1.getId(), actualItem.getId());
        assertEquals(item1.getName(), actualItem.getName());
        assertEquals(item1.getDescription(), actualItem.getDescription());
        assertEquals(item1.getAvailable(), actualItem.getAvailable());
        assertNull(actualItem.getLastBooking());
        assertNull(actualItem.getNextBooking());
        assertEquals(1, actualItem.getComments().size());
    }

    @Test
    void getItemWhenItemNotFoundThenException() {
        when(itemRepository.findById(3)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.getItem(1L, 3));
        assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void addItemWhenUserExistsAndValidItemDtoAndHasRequestThenPersistItemAndReturnItemDto() {
        ItemClientDto itemDto = new ItemClientDto("new item", "new description", true, 1);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(requestRepository.findById(1)).thenReturn(Optional.of(request1));
        when(itemRepository.save(any())).thenReturn(new Item(5, "new item", "new description", true, user2, request1));

        ItemServerDto actualItem = itemService.addItem(2L, itemDto);
        assertEquals(5, actualItem.getId());
        assertEquals(itemDto.getName(), actualItem.getName());
        assertEquals(itemDto.getDescription(), actualItem.getDescription());
        assertEquals(itemDto.getAvailable(), actualItem.getAvailable());
        assertEquals(itemDto.getRequestId(), actualItem.getRequestId());
    }

    @Test
    void addItemWhenUserExistsAndValidItemDtoAndNoRequestThenPersistItemAndReturnItemDto() {
        ItemClientDto itemDto = new ItemClientDto("new item", "new description", true, null);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.save(any())).thenReturn(new Item(5, "new item", "new description", true, user2, null));

        ItemServerDto actualItem = itemService.addItem(2L, itemDto);
        assertEquals(5, actualItem.getId());
        assertEquals(itemDto.getName(), actualItem.getName());
        assertEquals(itemDto.getDescription(), actualItem.getDescription());
        assertEquals(itemDto.getAvailable(), actualItem.getAvailable());
        assertNull(actualItem.getRequestId());

        verify(requestRepository, never()).findById(anyInt());
    }

    @Test
    void addItemWhenUserNotFoundThenException() {
        ItemClientDto itemDto = new ItemClientDto("new item", "new description", true, null);
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.addItem(3L, itemDto));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void addItemWhenRequestNotFoundThenException() {
        ItemClientDto itemDto = new ItemClientDto("new item", "new description", true, 2);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(requestRepository.findById(2)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> itemService.addItem(2L, itemDto));
        assertEquals("Запрос на вещь не найден", exception.getMessage());
        verify(itemRepository, never()).save(any());
    }

    @Test
    void editItemWhenUserAndItemExistAndValidDtoThenPersistItemAndReturnItemDto() {
        ItemClientDto itemDto = new ItemClientDto("new item", "new description", true, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(itemRepository.save(any())).thenReturn(new Item(1, "new item", "new description", true, user1, null));

        ItemServerDto actualItem = itemService.editItem(1L, 1, itemDto);
        assertEquals(item1.getId(), actualItem.getId());
        assertEquals(itemDto.getName(), actualItem.getName());
        assertEquals(itemDto.getDescription(), actualItem.getDescription());
        assertEquals(itemDto.getAvailable(), actualItem.getAvailable());
        assertEquals(itemDto.getRequestId(), actualItem.getRequestId());
    }

    @Test
    void editItemWhenUserAndItemExistAndNoNewFieldsThenPersistItemAndReturnItemDtoUnchanged() {
        ItemClientDto itemDto = new ItemClientDto(null, null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(itemRepository.save(any())).thenReturn(new Item(1, "test item 1", "test description 1", true, user1, null));

        ItemServerDto actualItem = itemService.editItem(1L, 1, itemDto);
        assertEquals(item1.getId(), actualItem.getId());
        assertEquals(item1.getName(), actualItem.getName());
        assertEquals(item1.getDescription(), actualItem.getDescription());
        assertEquals(item1.getAvailable(), actualItem.getAvailable());
        assertNull(actualItem.getRequestId());
    }

    @Test
    void editItemWhenUserIsNotOwnerThenException() {
        ItemClientDto itemDto = new ItemClientDto("new item", "new description", true, null);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> itemService.editItem(2L, 1, itemDto));
        assertEquals("Пользователь не является владельцем вещи", exception.getMessage());
    }

    @Test
    void editItemWhenEntityNotFoundThenException() {
        ItemClientDto itemDto = new ItemClientDto("new item", "new description", true, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        when(itemRepository.findById(5)).thenReturn(Optional.empty());
        // case: missing user
        EntityNotFoundException exception1 = assertThrows(EntityNotFoundException.class,
                () -> itemService.editItem(3L, 1, itemDto));
        assertEquals("Пользователь не найден", exception1.getMessage());
        // case: missing item
        EntityNotFoundException exception2 = assertThrows(EntityNotFoundException.class,
                () -> itemService.editItem(1L, 5, itemDto));
        assertEquals("Вещь не найдена", exception2.getMessage());

        verify(itemRepository, never()).save(any());
    }

    @Test
    void editItemWhenInvalidDtoThenException() {
        ItemClientDto itemDto = new ItemClientDto("", "", true, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        assertThrows(ConstraintViolationException.class,
                () -> itemService.editItem(1L, 1, itemDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void getItemsBySearchWhenHasSearchTextThenItemDtoList() {
        when(itemRepository.search("test", Pageable.ofSize(1))).thenReturn(List.of(item1, item2, item3, item4));
        List<ItemServerDto> actualItems = itemService.getItemsBySearch("test", 0, 1);
        assertEquals(4, actualItems.size());
    }

    @Test
    void getItemsBySearchWhenSearchTextMismatchesAnyItemThenEmptyList() {
        when(itemRepository.search("mismatch", Pageable.ofSize(1))).thenReturn(Collections.emptyList());
        List<ItemServerDto> actualItems = itemService.getItemsBySearch("mismatch", 0, 1);
        assertTrue(actualItems.isEmpty());
    }

    @Test
    void getItemsBySearchWhenEmptySearchTextThenEmptyListWithoutInvokingSearch() {
        List<ItemServerDto> actualItems = itemService.getItemsBySearch("", 0, 1);
        assertTrue(actualItems.isEmpty());
        verify(itemRepository, never()).search(anyString(), any());
    }

    @Test
    void addCommentWhenEntitiesExistAndValidCommentDtoThenPersistCommentAndReturnCommentDto() {
        CommentClientDto commentDto = new CommentClientDto("very good");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusAndEndBefore(anyInt(), anyLong(), any(), any()))
                .thenReturn(List.of(booking1));
        when(commentRepository.save(any())).thenReturn(new Comment(1, "very good", item1, user2, LocalDateTime.now()));

        CommentServerDto actualComment = itemService.addComment(2L, 1, commentDto);
        assertEquals(1, actualComment.getId());
        assertEquals(commentDto.getText(), actualComment.getText());
        assertEquals(2L, actualComment.getAuthorId());
    }

    @Test
    void addCommentWhenAuthorIsOwnerThenException() {
        CommentClientDto commentDto = new CommentClientDto("very good");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> itemService.addComment(1L, 1, commentDto));
        assertEquals("Владелец не может оставлять комментарии к собственной вещи", exception.getMessage());
    }

    @Test
    void addCommentWhenBookingsNotFoundThenException() {
        CommentClientDto commentDto = new CommentClientDto("very good");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item1));
        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusAndEndBefore(anyInt(), anyLong(), any(), any()))
                .thenReturn(Collections.emptyList());

        InvalidOperationException exception = assertThrows(InvalidOperationException.class,
                () -> itemService.addComment(2L, 1, commentDto));
        assertEquals("Пользователь, не бравший вещь в аренду, не может оставлять комментарии к ней",
                exception.getMessage());
    }

    @Test
    void addCommentWhenEntitiesNotFoundThenException() {
        CommentClientDto commentDto = new CommentClientDto("very good");
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        when(itemRepository.findById(5)).thenReturn(Optional.empty());

        // case: missing user
        EntityNotFoundException exception1 = assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(3L, 1, commentDto));
        assertEquals("Пользователь не найден", exception1.getMessage());
        // case: missing item
        EntityNotFoundException exception2 = assertThrows(EntityNotFoundException.class,
                () -> itemService.addComment(2L, 5, commentDto));
        assertEquals("Вещь не найдена", exception2.getMessage());

        verify(commentRepository, never()).save(any());
    }
}