package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class RequestClientDtoJsonTest {

    @Autowired
    private JacksonTester<RequestClientDto> json;

    @Test
    void testRequestClientDto() throws Exception {
        RequestClientDto requestClientDto = new RequestClientDto("description");

        JsonContent<RequestClientDto> result = json.write(requestClientDto);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }
}