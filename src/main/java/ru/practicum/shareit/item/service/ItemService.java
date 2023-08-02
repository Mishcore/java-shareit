package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(Integer itemId);

    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto editItem(Long ownerId, Integer itemId, ItemDto itemDto);

    List<ItemDto> getAllUserItems(Long ownerId);

    List<ItemDto> getItemsBySearch(String text);

}
