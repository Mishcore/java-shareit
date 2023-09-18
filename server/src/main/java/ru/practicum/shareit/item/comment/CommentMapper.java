package ru.practicum.shareit.item.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public static Comment toComment(User author, Item item, CommentClientDto commentDto) {
        return new Comment(
                null,
                commentDto.getText(),
                item,
                author,
                LocalDateTime.now()
        );
    }

    public static CommentServerDto toCommentServerDto(Comment comment) {
        return new CommentServerDto(
                comment.getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated()
        );
    }
}
