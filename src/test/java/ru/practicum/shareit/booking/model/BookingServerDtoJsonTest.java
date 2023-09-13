package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.user.model.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingServerDtoJsonTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private JacksonTester<BookingServerDto> json;

    @Test
    void testBookingServerDto() throws Exception {
        UserDto userDto = new UserDto(1L, "name", "mail@mail.ru");
        ItemServerDto itemServerDto = new ItemServerDto(1, "item", "description", true, 1, null, null, new ArrayList<>());
        BookingServerDto bookingServerDto = new BookingServerDto(
                1, userDto, itemServerDto, LocalDateTime.of(2000, 1, 1, 0, 0, 0),
                LocalDateTime.of(2000, 1, 2, 0, 0, 0), Status.WAITING);

        JsonContent<BookingServerDto> result = json.write(bookingServerDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);

        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("mail@mail.ru");

        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.item.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.lastBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.item.nextBooking").isNull();
        assertThat(result).extractingJsonPathValue("$.item.comments").isInstanceOf(ArrayList.class);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(LocalDateTime.of(2000, 1, 1, 0, 0, 0).format(FORMATTER));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(LocalDateTime.of(2000, 1, 2, 0, 0, 0).format(FORMATTER));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.toString());
    }
}