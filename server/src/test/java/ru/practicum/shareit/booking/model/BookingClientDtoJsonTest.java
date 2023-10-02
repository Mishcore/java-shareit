package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingClientDtoJsonTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private JacksonTester<BookingClientDto> json;

    @Test
    void testBookingClientDto() throws Exception {
        BookingClientDto bookingClientDto =
                new BookingClientDto(1, LocalDateTime.of(2000, 1, 1, 0, 0, 0), LocalDateTime.of(2000, 1, 2, 0, 0, 0));

        JsonContent<BookingClientDto> result = json.write(bookingClientDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(LocalDateTime.of(2000, 1, 1, 0, 0, 0).format(FORMATTER));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(LocalDateTime.of(2000, 1, 2, 0, 0, 0).format(FORMATTER));
    }
}