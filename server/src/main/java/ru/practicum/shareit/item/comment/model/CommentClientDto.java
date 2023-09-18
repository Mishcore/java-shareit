package ru.practicum.shareit.item.comment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class CommentClientDto {
    private String text;

    @JsonCreator
    public CommentClientDto(String text) {
        this.text = text;
    }
}
