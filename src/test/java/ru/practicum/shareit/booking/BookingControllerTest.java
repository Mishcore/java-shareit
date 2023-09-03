package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.booking.model.BookingServerDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.user.model.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ru.practicum.shareit.Constants.USER_ID;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private final BookingServerDto bookingServerDto = new BookingServerDto(
            1,
            new UserDto(1L, "testUser", "test@mail.ru"),
            new ItemServerDto(1, "testItem", "test", true, null, null, null, new ArrayList<>()),
            LocalDateTime.of(2024, 1, 1, 0, 0, 0),
            LocalDateTime.of(2024, 1, 2, 0, 0, 0),
            Status.WAITING
    );

    private final BookingClientDto bookingClientDto = new BookingClientDto(
            1,
            LocalDateTime.of(2024, 1, 1, 0, 0, 0),
            LocalDateTime.of(2024, 1, 2, 0, 0, 0)
    );

    @SneakyThrows
    @Test
    void addBookingWhenInvokedThenStatusOkAndBookingDto() {
        when(bookingService.addBooking(anyLong(), any())).thenReturn(bookingServerDto);

        mvc.perform(post("/bookings")
                .header(USER_ID, 2L)
                .content(mapper.writeValueAsString(bookingClientDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingServerDto)));

        verify(bookingService, times(1)).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addBookingWhenNoSuchUserFoundThenStatusNotFound() {
        when(bookingService.addBooking(anyLong(), any())).thenThrow(EntityNotFoundException.class);

        mvc.perform(post("/bookings")
                        .header(USER_ID, 3L)
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addBookingWhenInvalidDtoThenStatusBadRequest() {
        bookingClientDto.setEnd(bookingClientDto.getStart());

        mvc.perform(post("/bookings")
                        .header(USER_ID, 2L)
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -999L})
    void addBookingWhenInvalidUserIdThenStatusBadRequest(long userId) {
        mvc.perform(post("/bookings")
                        .header(USER_ID, userId)
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void approveBookingWhenInvokedThenStatusOkAndBookingDto() {
        when(bookingService.approveBooking(anyLong(), anyInt(), anyBoolean())).thenReturn(bookingServerDto);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                .header(USER_ID, 2L)
                .param("approved", "true")
                .content(mapper.writeValueAsString(bookingClientDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingServerDto)));

        verify(bookingService, times(1)).approveBooking(anyLong(), anyInt(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void approveBookingWhenNoSuchEntityFoundThenStatusNotFound() {
        when(bookingService.approveBooking(anyLong(), anyInt(), anyBoolean())).thenThrow(EntityNotFoundException.class);

        mvc.perform(patch("/bookings/{bookingId}", 1)
                        .header(USER_ID, 2L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).approveBooking(anyLong(), anyInt(), anyBoolean());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void approveBookingWhenInvalidIdThenStatusBadRequest(long userId, int bookingId) {
        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_ID, userId)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingClientDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(anyLong(), anyInt(), anyBoolean());
    }

    @SneakyThrows
    @Test
    void getBookingWhenInvokedThenStatusOkAndBookingDto() {
        when(bookingService.getBooking(anyLong(), anyInt())).thenReturn(bookingServerDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                .header(USER_ID, 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingServerDto)));

        verify(bookingService, times(1)).getBooking(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getBookingWhenNoSuchEntityFound() {
        when(bookingService.getBooking(anyLong(), anyInt())).thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header(USER_ID, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getBooking(anyLong(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"0,1", "-1,1", "-999,1", "1,0", "1,-1", "-1,-999"})
    void getBookingWhenInvalidIdThenStatusBadRequest(long userId, int bookingId) {
        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_ID, userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getBooking(anyLong(), anyInt());
    }

    @SneakyThrows
    @Test
    void getUserBookingsWhenInvokedThenStatusOkAndBookingDtoList() {
        when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingServerDto));

        mvc.perform(get("/bookings")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingServerDto))));

        verify(bookingService, times(1)).getUserBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getUserBookingsWhenNoSuchUserFoundThenStatusNotFound() {
        when(bookingService.getUserBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/bookings")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getUserBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"-1,1,1", "1,-1,1", "1,1,-1"})
    void getUserBookingsWhenInvalidHeaderOrParameterThenStatusBadRequest(long userId, int from, int size) {
        mvc.perform(get("/bookings")
                        .header(USER_ID, userId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getUserBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemBookingsWhenInvokedThenStatusOkAndBookingDtoList() {
        when(bookingService.getItemBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingServerDto));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingServerDto))));

        verify(bookingService, times(1)).getItemBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemBookingsWhenNoSuchUserFoundThenStatusNotFound() {
        when(bookingService.getItemBookings(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(EntityNotFoundException.class);

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).getItemBookings(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @ParameterizedTest
    @CsvSource({"-1,1,1", "1,-1,1", "1,1,-1"})
    void getItemBookingsWhenInvalidHeaderOrParameterThenStatusBadRequest(long userId, int from, int size) {
        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, userId)
                        .param("state", "ALL")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getItemBookings(anyLong(), any(), anyInt(), anyInt());
    }
}