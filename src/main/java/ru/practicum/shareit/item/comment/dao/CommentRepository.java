package ru.practicum.shareit.item.comment.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemId(Integer itemId);
}
