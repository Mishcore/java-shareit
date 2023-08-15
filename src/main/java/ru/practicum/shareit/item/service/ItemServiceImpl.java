package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public List<ItemDto> getAllUserItems(Long ownerId) {
        List<ItemDto> userItems = itemDao.getAllUserItems(userDao.getUser(ownerId).getId());
        log.info("Получен список всех вещей пользователя ID " + ownerId);
        return userItems;
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        ItemDto itemDto = itemDao.getItem(itemId);
        log.info("Получена вещь ID " + itemId);
        return itemDto;
    }

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        ItemDto addedItemDto = itemDao.addItem(userDao.getUser(ownerId).getId(), itemDto);
        log.info("Добавлена новая вещь ID " + addedItemDto.getId() + " пользователя ID" + ownerId);
        return addedItemDto;
    }

    @Override
    public ItemDto editItem(Long ownerId, Integer itemId, ItemDto itemDto) {
        ItemDto editedItem = itemDao.editItem(userDao.getUser(ownerId).getId(), itemId, itemDto);
        log.info("Отредактированы данные вещи ID " + itemId + " пользователя ID " + ownerId);
        return editedItem;
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        if (text.isBlank()) {
            log.info("Строка поиска пуста. Возвращён пустой список");
            return Collections.emptyList();
        }
        List<ItemDto> items = itemDao.getItemsBySearch(text);
        log.info("Получен список вещей по поисковой строке \"" + text + "\"");
        return items;
    }
}