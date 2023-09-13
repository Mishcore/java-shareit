package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.request.model.RequestServerDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class RequestServiceTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private final User user1 = new User(1L, "test1", "test@mail.ru");
    private final User user2 = new User(2L, "test2", "test@mail.com");

    private final Request request1
     = new Request(1, "test request 1", user1, LocalDateTime.of(2000, 1, 1, 0, 0, 0));
    private final Request request2
     = new Request(2, "test request 2", user1, LocalDateTime.of(2000, 1, 2, 0, 0, 0));

     private final Item item1 = new Item(1, "test item", "test item descr", true, user2, request2);

    @Test
    void addRequestWhenUserExistsAndValidRequestDtoThenPersistRequestAndReturnRequestDto() {
        RequestClientDto requestDto = new RequestClientDto("new request");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(requestRepository.save(any())).thenReturn(new Request(2, "new request", user1, LocalDateTime.now()));
        RequestServerDto actualRequest = requestService.addRequest(1L, requestDto);

        assertEquals(2, actualRequest.getId());
        assertEquals(requestDto.getDescription(), actualRequest.getDescription());
        assertNotNull(actualRequest.getCreated());
        assertNull(actualRequest.getItems());
    }

    @Test
    void addRequestWhenUserNotFoundThenException() {
        RequestClientDto request2 = new RequestClientDto("new request");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.addRequest(1L, request2));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getUserRequestsWhenUserAndRequestsExistThenRequestDtoList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request2, request1));
        when(itemRepository.findAllByRequestId(1)).thenReturn(Collections.emptyList());
        when(itemRepository.findAllByRequestId(2)).thenReturn(List.of(item1));

        List<RequestServerDto> actualRequestDtos = requestService.getUserRequests(1L);
        assertEquals(2, actualRequestDtos.size());
        assertEquals(request2.getId(), actualRequestDtos.get(0).getId());
        assertEquals(request2.getDescription(), actualRequestDtos.get(0).getDescription());
        assertEquals(request2.getCreated(), actualRequestDtos.get(0).getCreated());
        assertEquals(1, actualRequestDtos.get(0).getItems().size());
        assertEquals(request1.getId(), actualRequestDtos.get(1).getId());
        assertEquals(request1.getDescription(), actualRequestDtos.get(1).getDescription());
        assertEquals(request1.getCreated(), actualRequestDtos.get(1).getCreated());
        assertEquals(0, actualRequestDtos.get(1).getItems().size());
    }

    @Test
    void getUserRequestsWhenUserExistsAndNoRequestsThenEmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(Collections.emptyList());

        List<RequestServerDto> actualRequestDtos = assertDoesNotThrow(() -> requestService.getUserRequests(1L));
        assertTrue(actualRequestDtos.isEmpty());
    }

    @Test
    void getUserRequestsWhenUserNotFoundThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.getUserRequests(1L));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).findAllByRequestorIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getOtherRequestsWhenUserAndRequestsExistThenRequestDtoList() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(requestRepository.findAllByRequestorIdNot(2L, PageRequest.of(0, 1, Sort.by("created").descending())))
            .thenReturn(List.of(request2, request1));
        when(itemRepository.findAllByRequestId(anyInt())).thenReturn(Collections.emptyList());

        List<RequestServerDto> actualRequestDtos = requestService.getOtherRequests(2L, 0, 1);
        assertEquals(2, actualRequestDtos.size());
        assertEquals(request2.getId(), actualRequestDtos.get(0).getId());
        assertEquals(request2.getDescription(), actualRequestDtos.get(0).getDescription());
        assertEquals(request2.getCreated(), actualRequestDtos.get(0).getCreated());
        assertEquals(request1.getId(), actualRequestDtos.get(1).getId());
        assertEquals(request1.getDescription(), actualRequestDtos.get(1).getDescription());
        assertEquals(request1.getCreated(), actualRequestDtos.get(1).getCreated());
    }

    @Test
    void getOtherRequestsWhenUserExistsAndNoRequestsThenEmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(requestRepository.findAllByRequestorIdNot(1L, PageRequest.of(0, 1, Sort.by("created").descending())))
            .thenReturn(Collections.emptyList());

        List<RequestServerDto> actualRequestDtos = assertDoesNotThrow(() -> requestService.getOtherRequests(1L, 0, 1));
        assertTrue(actualRequestDtos.isEmpty());
    }

    @Test
    void getOtherRequestsWhenUserNotFoundThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.getOtherRequests(1L, 0, 1));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).findAllByRequestorIdNot(anyLong(), any());
    }

    @Test
    void getRequestWhenUserAndRequestExistThenRequestDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(requestRepository.findById(2)).thenReturn(Optional.of(request2));
        when(itemRepository.findAllByRequestId(2)).thenReturn(List.of(item1));

        RequestServerDto actualRequest = requestService.getRequest(1L, 2);
        assertEquals(request2.getDescription(), actualRequest.getDescription());
        assertEquals(request2.getCreated(), actualRequest.getCreated());
    }

    @Test
    void getRequestWhenUserNotFoundThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequest(1L, 2));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(requestRepository, never()).findById(anyInt());
    }

    @Test
    void getRequestWhenUserExistsAndRequestNotFoundThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(requestRepository.findById(2)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> requestService.getRequest(1L, 2));
        assertEquals("Запрос на вещь не найден", exception.getMessage());
    }
}