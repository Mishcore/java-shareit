package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnPatch;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.Constants.USER_ID;

@Slf4j
@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(USER_ID) @Positive long ownerId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение всех вещей пользователя, ownerId={}", ownerId);
        return itemClient.getAllUserItems(ownerId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) @Positive long userId,
                                          @PathVariable @Positive int itemId) {
        log.info("Запрос на получение вещи, itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsBySearch(@RequestParam String text,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Запрос на получение списка вещей по поисковой строке, text={}", text);
        return itemClient.getItemsBySearch(text, from, size);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID) @Positive long ownerId,
                                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на добавление новой вещи, ownerId={}, item={}", ownerId, itemDto);
        return itemClient.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated(OnPatch.class)
    public ResponseEntity<Object> editItem(@RequestHeader(USER_ID) @Positive long ownerId,
                                           @PathVariable @Positive int itemId,
                                           @RequestBody @Valid ItemDto itemDto) {
        log.info("Запрос на редактирование вещи, ownerId={}, itemId={}", ownerId, itemId);
        return itemClient.editItem(ownerId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(USER_ID) @Positive long authorId,
                                             @PathVariable @Positive int itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Запрос на добавление комментария к вещи, authorId={}, itemId={}, comment={}",
                authorId, itemId, commentDto);
        return itemClient.addComment(authorId, itemId, commentDto);
    }
}
