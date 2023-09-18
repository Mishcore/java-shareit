package ru.practicum.shareit.item.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentServerDto {
    private Integer id;
    private Long authorId;
    private String authorName;
    private String text;
    private LocalDateTime created;
}
