package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.request.model.RequestServerDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Constants.USER_ID;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService requestService;

    private final RequestClientDto requestClientDto = new RequestClientDto("test");
    private final RequestServerDto requestServerDto
            = new RequestServerDto(1, "test", LocalDateTime.of(2000, 1, 1, 0, 0, 0), Collections.emptyList());

    @SneakyThrows
    @Test
    void addRequestWhenInvokedThenStatusOkAndRequestDto() {
        when(requestService.addRequest(anyLong(), any())).thenReturn(requestServerDto);

        mvc.perform(post("/requests")
                    .header(USER_ID, 1L)
                    .content(mapper.writeValueAsString(requestClientDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestServerDto)));

        verify(requestService, times(1)).addRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getUserRequestsWhenInvokedThenStatusOkAndRequestDtoList() {
        when(requestService.getUserRequests(anyLong())).thenReturn(List.of(requestServerDto));

        mvc.perform(get("/requests")
                    .header(USER_ID, 1L)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestServerDto))));

        verify(requestService, times(1)).getUserRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void getUserRequestsWhenNoSuchUserFoundThenStatusNotFound() {
        when(requestService.getUserRequests(anyLong())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/requests")
                    .header(USER_ID, 1L)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getUserRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void getOtherRequestsWhenInvokedThenStatusOkAndRequestDtoList() {
        when(requestService.getOtherRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestServerDto));

        mvc.perform(get("/requests/all")
                        .header(USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(requestServerDto))));

        verify(requestService, times(1)).getOtherRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getOtherRequestsWhenNoSuchUserFoundThenStatusOkAndRequestDtoList() {
        when(requestService.getOtherRequests(anyLong(), anyInt(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/requests/all")
                        .header(USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getOtherRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getRequestWhenInvokedThenStatusOkAndRequestDto() {
        when(requestService.getRequest(anyLong(), anyInt())).thenReturn(requestServerDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(requestServerDto)));

        verify(requestService, times(1)).getRequest(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getRequestWhenNoSuchUserOrRequestFoundThenStatusNotFound() {
        when(requestService.getRequest(anyLong(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(requestService, times(1)).getRequest(anyLong(), anyInt());
    }
}