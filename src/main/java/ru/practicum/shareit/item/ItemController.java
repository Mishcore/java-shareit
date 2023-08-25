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

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemServerDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId) {
        log.info("Принят запрос на получение списка всех вещей пользователя ID " + ownerId);
        return itemService.getAllUserItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemServerDto getItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                 @PathVariable @Positive Integer itemId) {
        log.info("Принят запрос на получение вещи ID " + itemId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemServerDto> getItemsBySearch(@RequestParam String text) {
        log.info("Принят запрос на получение списка вещей по поисковой строке \"" + text + "\"");
        return itemService.getItemsBySearch(text);
    }

    @PostMapping
    public ItemServerDto addItem(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                 @RequestBody @Valid ItemClientDto itemDto) {
        log.info("Принят запрос на добавление новой вещи пользователя ID " + ownerId);
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemServerDto editItem(
            @RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
            @PathVariable @Positive Integer itemId,
            @RequestBody ItemClientDto itemDto) {
        log.info("Принят запрос на редактирование данных вещи ID " + itemId + " пользователя ID " + ownerId);
        return itemService.editItem(ownerId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentServerDto addComment(@RequestHeader("X-Sharer-User-Id") @Positive Long authorId,
                                       @PathVariable @Positive Integer itemId,
                                       @RequestBody @Valid CommentClientDto commentDto) {
        log.info("Принят запрос на добавление комментария к вещи ID " + itemId);
        return itemService.addComment(authorId, itemId, commentDto);
    }
}
