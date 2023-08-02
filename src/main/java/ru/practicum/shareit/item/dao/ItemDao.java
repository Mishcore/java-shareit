package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

public interface ItemDao {

    ItemDto getItem(Integer itemId);

    ItemDto addItem(Long userId, ItemDto item);

    ItemDto editItem(Long ownerId, Integer itemId, ItemDto itemDto);

    List<ItemDto> getAllUserItems(@Positive Long ownerId);

    List<ItemDto> getItemsBySearch(@NotBlank String text);
}