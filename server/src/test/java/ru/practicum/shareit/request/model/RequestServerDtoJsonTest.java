package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestServerDtoJsonTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private JacksonTester<RequestServerDto> json;

    @Test
    void testRequestServerDto() throws Exception {
        RequestServerDto requestServerDto = new RequestServerDto(
                1, "description", LocalDateTime.of(2000, 1, 1, 0, 0, 0), new ArrayList<>());
        JsonContent<RequestServerDto> result = json.write(requestServerDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(LocalDateTime.of(2000, 1, 1, 0, 0, 0).format(FORMATTER));
        assertThat(result).extractingJsonPathValue("$.items").isInstanceOf(ArrayList.class);
    }
}