package ru.practicum.shareit.item.comment.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentClientDtoJsonTest {

    @Autowired
    private JacksonTester<CommentClientDto> json;

    @Test
    void testCommentClientDto() throws Exception {
        CommentClientDto commentClientDto = new CommentClientDto("text");

        JsonContent<CommentClientDto> result = json.write(commentClientDto);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
    }
}