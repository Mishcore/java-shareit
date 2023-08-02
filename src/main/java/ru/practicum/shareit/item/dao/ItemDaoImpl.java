package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedEdditingException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ItemDaoImpl implements ItemDao {
    private int id = 0;
    private final Map<Integer, Item> items  = new HashMap<>();

    @Override
    public ItemDto getItem(Integer itemId) {
        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException("Вещь не найдена");
        }
        return ItemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        itemDto.setId(++id);
        items.put(itemDto.getId(), ItemMapper.toItem(ownerId, itemDto));
        return ItemMapper.toItemDto(items.get(itemDto.getId()));
    }

    @Override
    public ItemDto editItem(Long ownerId, Integer itemId, ItemDto itemDto) {
        if (!items.containsKey(itemId)) {
            throw new EntityNotFoundException("Вещь не найдена");
        }
        if (!items.get(itemId).getOwner().equals(ownerId)) {
            throw new UnauthorizedEdditingException("Вещь может редактировать только её владелец");
        }

        Item oldItem = items.get(itemId);
        Item editedItem = new Item(itemId, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), ownerId, null);

        if (editedItem.getName() == null) {
            editedItem.setName(oldItem.getName());
        }
        if (editedItem.getDescription() == null) {
            editedItem.setDescription(oldItem.getDescription());
        }
        if (editedItem.getAvailable() == null) {
            editedItem.setAvailable(oldItem.getAvailable());
        }

        validateItem(editedItem);
        items.replace(itemId, editedItem);
        return ItemMapper.toItemDto(editedItem);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long ownerId) {
        return items.values().stream()
        .filter(item -> item.getOwner().equals(ownerId))
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        return items.values().stream()
        .filter(Item::getAvailable)
        .filter(item ->
            item.getName().toLowerCase().contains(text.toLowerCase()) ||
            item.getDescription().toLowerCase().contains(text.toLowerCase())
            )
        .map(ItemMapper::toItemDto)
        .collect(Collectors.toList());
    }

    private void validateItem(Item item) {
        Set<ConstraintViolation<Item>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(item);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
