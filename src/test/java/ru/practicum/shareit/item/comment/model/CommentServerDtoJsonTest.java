package ru.practicum.shareit.item.comment.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentServerDtoJsonTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private JacksonTester<CommentServerDto> json;

    @Test
    void testCommentServerDto() throws Exception {
        CommentServerDto commentServerDto = new CommentServerDto(1, 1L, "name", "text", LocalDateTime.of(2000, 1, 1, 0, 0, 0));

        JsonContent<CommentServerDto> result = json.write(commentServerDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(LocalDateTime.of(2000, 1, 1, 0, 0, 0).format(FORMATTER));
    }
}