package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.USER_ID;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private final ItemClientDto itemClientDto =
            new ItemClientDto("test", "test description", true, null);
    private final ItemServerDto itemServerDto =
            new ItemServerDto(1, "test", "test description", true, null, null, null, new ArrayList<>());
    private final CommentClientDto commentClientDto =
            new CommentClientDto("test comment");
    private final CommentServerDto commentServerDto =
            new CommentServerDto(1, 1L, "test name", "test comment", LocalDateTime.of(2000, 1, 1, 0, 0, 0));

    @SneakyThrows
    @Test
    void getAllUserItemsWhenInvokedThenStatusOkAndItemDtoList() {
        when(itemService.getAllUserItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemServerDto));

        mvc.perform(get("/items")
                .header(USER_ID, 1L)
                .param("from", "0")
                .param("size", "1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemServerDto))));

        verify(itemService, times(1)).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getAllUserItemsWhenNoSuchUserFoundThenStatusNotFound() {
        when(itemService.getAllUserItems(anyLong(), anyInt(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/items")
                        .header(USER_ID, 3L)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getAllUserItems(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemWhenInvokedThenStatusOkAndItemDto() {
        when(itemService.getItem(anyLong(), anyInt())).thenReturn(itemServerDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemServerDto)));

        verify(itemService, times(1)).getItem(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemWhenNoSuchEntityFoundThenStatusNotFound() {
        when(itemService.getItem(anyLong(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/items/{itemId}", 2)
                        .header(USER_ID, 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).getItem(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemsBySearchWhenInvokedThenStatusOkAndItemDtoList() {
        when(itemService.getItemsBySearch(anyString(), anyInt(), anyInt())).thenReturn(List.of(itemServerDto));

        mvc.perform(get("/items/search")
                    .param("text", "search")
                    .param("from", "0")
                    .param("size", "1")
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemServerDto))));

        verify(itemService, times(1)).getItemsBySearch(anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void addItemWhenInvokedThenStatusOkAndItemDto() {
        when(itemService.addItem(anyLong(), any())).thenReturn(itemServerDto);

        mvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemServerDto)));

        verify(itemService, times(1)).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addItemWhenNoSuchUserFoundThenStatusNotFound() {
        when(itemService.addItem(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/items")
                        .header(USER_ID, 3L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).addItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void editItemWhenInvokedThenStatusOkAndItemDto() {
        when(itemService.editItem(anyLong(), anyInt(), any())).thenReturn(itemServerDto);

        mvc.perform(patch("/items/{itemId}", 1)
                .header(USER_ID, 1L)
                .content(mapper.writeValueAsString(itemClientDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemServerDto)));

        verify(itemService, times(1)).editItem(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void editItemWhenNoSuchEntityFoundThenStatusNotFound() {
        when(itemService.editItem(anyLong(), anyInt(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header(USER_ID, 1L)
                        .content(mapper.writeValueAsString(itemClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).editItem(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void addCommentWhenInvokedThenStatusOkAndCommentDto() {
        when(itemService.addComment(anyLong(), anyInt(), any())).thenReturn(commentServerDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                .header(USER_ID, 1L)
                .content(mapper.writeValueAsString(commentClientDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentServerDto)));

        verify(itemService, times(1)).addComment(anyLong(), anyInt(), any());
    }

    @SneakyThrows
    @Test
    void addCommentWhenNoSuchEntityFoundThenStatusNotFound() {
        when(itemService.addComment(anyLong(), anyInt(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header(USER_ID, 1L)
                        .content(mapper.writeValueAsString(commentClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(itemService, times(1)).addComment(anyLong(), anyInt(), any());
    }
}