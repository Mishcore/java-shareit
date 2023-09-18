package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.comment.model.CommentServerDto;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemServerDto> getAllUserItems(
            @RequestHeader(USER_ID) Long ownerId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Принят запрос на получение списка всех вещей пользователя ID " + ownerId);
        return itemService.getAllUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemServerDto getItem(@RequestHeader(USER_ID) Long userId,
                                 @PathVariable Integer itemId) {
        log.info("Принят запрос на получение вещи ID " + itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemServerDto> getItemsBySearch(
            @RequestParam String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Принят запрос на получение списка вещей по поисковой строке \"" + text + "\"");
        return itemService.getItemsBySearch(text, from, size);
    }

    @PostMapping
    public ItemServerDto addItem(@RequestHeader(USER_ID) Long ownerId,
                                 @RequestBody ItemClientDto itemDto) {
        log.info("Принят запрос на добавление новой вещи пользователя ID " + ownerId);
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemServerDto editItem(
            @RequestHeader(USER_ID) Long ownerId,
            @PathVariable Integer itemId,
            @RequestBody ItemClientDto itemDto) {
        log.info("Принят запрос на редактирование данных вещи ID " + itemId + " пользователя ID " + ownerId);
        return itemService.editItem(ownerId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentServerDto addComment(@RequestHeader(USER_ID) Long authorId,
                                       @PathVariable Integer itemId,
                                       @RequestBody CommentClientDto commentDto) {
        log.info("Принят запрос на добавление комментария к вещи ID " + itemId);
        return itemService.addComment(authorId, itemId, commentDto);
    }
}
