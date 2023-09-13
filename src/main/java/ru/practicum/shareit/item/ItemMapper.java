package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class ItemMapper {
    public static ItemServerDto toItemServerDto(Item item) {
        Integer requestId = null;
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }
        return new ItemServerDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId,
                null,
                null,
                null
        );
    }

    public static Item toItem(User itemUser, Request request, ItemClientDto itemDto) {
        return new Item(
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemUser,
                request
        );
    }
}
