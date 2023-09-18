package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.request.model.RequestServerDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static ru.practicum.shareit.Constants.USER_ID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestServerDto addRequest(
            @RequestHeader(USER_ID) Long userId, @RequestBody RequestClientDto requestDto) {
        log.info("Принят запрос на добавление запроса вещи");
        return requestService.addRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestServerDto> getUserRequests(@RequestHeader(USER_ID) Long userId) {
        log.info("Принят запрос на получение списка запросов пользователя ID " + userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestServerDto> getOtherRequests(
            @RequestHeader(USER_ID) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Принят запрос на получение запросов других пользователей");
        return requestService.getOtherRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestServerDto getRequest(@RequestHeader(USER_ID) Long userId,
                                       @PathVariable Integer requestId) {
        log.info("Принят запрос на получение запроса ID " + requestId);
        return requestService.getRequest(userId, requestId);
    }
}
