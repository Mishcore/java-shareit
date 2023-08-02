package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
        item.getId(),
        item.getName(),
        item.getDescription(),
        item.getAvailable()
        );
    }

    public static Item toItem(Long ownerId, ItemDto itemDto) {
        return new Item(
            itemDto.getId(),
            itemDto.getName(),
            itemDto.getDescription(),
            itemDto.getAvailable(),
            ownerId,
            null
        );
    }
}
