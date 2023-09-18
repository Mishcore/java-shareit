package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

import static ru.practicum.shareit.Constants.USER_ID;

@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(USER_ID) long ownerId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на получение всех вещей пользователя, ownerId={}", ownerId);
        return itemClient.getAllUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) long userId,
                                          @PathVariable int itemId) {
        log.info("Запрос на получение вещи, itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam String text,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("Запрос на получение списка вещей по поисковой строке, text={}", text);
        return itemClient.getItemsBySearch(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) long ownerId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на добавление новой вещи, ownerId={}, item={}", ownerId, itemDto);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader(USER_ID) long ownerId,
                                           @PathVariable int itemId,
                                           @RequestBody ItemDto itemDto) {
        log.info("Запрос на редактирование вещи, ownerId={}, itemId={}", ownerId, itemId);
        validateItemDto(itemDto);
        return itemClient.editItem(ownerId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) long authorId,
                                             @PathVariable int itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Запрос на добавление комментария к вещи, authorId={}, itemId={}, comment={}",
                authorId, itemId, commentDto);
        return itemClient.addComment(authorId, itemId, commentDto);
    }

    private void validateItemDto(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        if (itemDto.getDescription() != null && itemDto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }
    }
}
