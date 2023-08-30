package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.request.model.RequestServerDto;

import java.util.List;

public interface RequestService {
    RequestServerDto addRequest(Long userId, RequestClientDto requestDto);

    List<RequestServerDto> getUserRequests(Long userId);

    List<RequestServerDto> getOtherRequests(Long userId, Integer from, Integer size);

    RequestServerDto getRequest(Long userId, Integer requestId);
}
