package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    private final UserDto userDto = new UserDto(1L, "tested", "test@mail.ru");

    @SneakyThrows
    @Test
    void getAllUsersWhenInvokedThenStatusOkAndUserList() {
        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));

        verify(userService, times(1)).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUserWhenInvokedThenStatusOkAndUser() {
        when(userService.getUser(anyLong())).thenReturn(userDto);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userService, times(1)).getUser(anyLong());
    }

    @SneakyThrows
    @Test
    void getUserWhenNoSuchUserFoundThenStatusNotFound() {
        when(userService.getUser(anyLong())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/users/{userId}", 2L))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUser(anyLong());
    }

    @SneakyThrows
    @Test
    void addUserWhenInvokedThenStatusOkAndUser() {
        when(userService.addUser(any())).thenReturn(userDto);

        mvc.perform(post("/users")
                    .content(mapper.writeValueAsString(userDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userService, times(1)).addUser(any());
    }

    @SneakyThrows
    @Test
    void addUserWhenEmailAlreadyExistsThenStatusConflict() {
        when(userService.addUser(any())).thenThrow(EmailAlreadyExistsException.class);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userService, times(1)).addUser(any());
    }

    @SneakyThrows
    @Test
    void updateUserWhenInvokedThenStatusOkAndUser() {
        when(userService.updateUser(anyLong(), any())).thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userDto)));

        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUserWhenNoSuchUserFoundThenStatusNotFound() {
        when(userService.updateUser(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(patch("/users/{userId}", 2L)
                    .content(mapper.writeValueAsString(userDto))
                    .characterEncoding(StandardCharsets.UTF_8)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateUserWhenEmailAlreadyExistsThenStatusConflict() {
        when(userService.updateUser(anyLong(), any())).thenThrow(EmailAlreadyExistsException.class);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void deleteUserWhenInvokedThenStatusOk() {
        doNothing().when(userService).deleteUser(anyLong());

        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @SneakyThrows
    @Test
    void deleteUserWhenNoSuchUserFoundThenStatusNotFound() {
        doThrow(EntityNotFoundException.class).when(userService).deleteUser(anyLong());

        mvc.perform(delete("/users/{userId}", 2L))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).deleteUser(anyLong());
    }
}