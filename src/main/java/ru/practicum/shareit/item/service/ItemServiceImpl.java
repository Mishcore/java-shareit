package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto getItem(Integer itemId) {
        return itemDao.getItem(itemId);
    }

    @Override
    public ItemDto addItem(Long ownerId, ItemDto itemDto) {
        return itemDao.addItem(userDao.getUser(ownerId).getId(), itemDto);
    }

    @Override
    public ItemDto editItem(Long ownerId, Integer itemId, ItemDto itemDto) {
        return itemDao.editItem(userDao.getUser(ownerId).getId(), itemId, itemDto);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long ownerId) {
        return itemDao.getAllUserItems(userDao.getUser(ownerId).getId());
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        return itemDao.getItemsBySearch(text);
    }
}