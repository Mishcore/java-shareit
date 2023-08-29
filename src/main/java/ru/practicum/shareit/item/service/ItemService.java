package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;

import java.util.List;

public interface ItemService {

    ItemServerDto getItem(Long userId, Integer itemId);

    ItemServerDto addItem(Long userId, ItemClientDto itemDto);

    ItemServerDto editItem(Long ownerId, Integer itemId, ItemClientDto itemDto);

    List<ItemServerDto> getAllUserItems(Long ownerId);

    List<ItemServerDto> getItemsBySearch(String text);

    CommentServerDto addComment(Long authorId, Integer itemId, CommentClientDto commentDto);
}
