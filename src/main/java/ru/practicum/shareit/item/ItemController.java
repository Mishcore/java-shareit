package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId) {
        log.info("Принят запрос на получение списка всех вещей пользователя");
        return itemService.getAllUserItems(ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable @Positive Integer itemId) {
        log.info("Принят запрос на получение вещи");
        return itemService.getItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam String text) {
        if (text.isBlank()) {
            log.info("Принят запрос на получение списка вещей по пустой поисковой строке. Будет возвращён пустой список");
            return Collections.emptyList();
        }
        log.info("Принят запрос на получение списка вещей по поисковой строке \"" + text + "\"");
        return itemService.getItemsBySearch(text);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId, @RequestBody @Validated ItemDto itemDto) {
        log.info("Принят запрос на добавление новой вещи пользователя");
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto editItem(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId, @PathVariable @Positive Integer itemId, @RequestBody ItemDto itemDto) {
        log.info("Принят запрос на редактирование данных вещи пользователя");
        return itemService.editItem(ownerId, itemId, itemDto);
    }
}
