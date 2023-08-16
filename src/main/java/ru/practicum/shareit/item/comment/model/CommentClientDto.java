package ru.practicum.shareit.item.comment.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentClientDto {

    @NotBlank
    private String text;

    @JsonCreator
    public CommentClientDto(String text) {
        this.text = text;
    }
}
