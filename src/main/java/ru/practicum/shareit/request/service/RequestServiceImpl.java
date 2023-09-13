package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.request.model.RequestServerDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.EntityFinder.findRequestOrThrowException;
import static ru.practicum.shareit.EntityFinder.findUserOrThrowException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    @Override
    public RequestServerDto addRequest(Long userId, RequestClientDto requestDto) {
        User user = findUserOrThrowException(userRepo, userId);
        Request request = requestRepo.save(RequestMapper.toRequest(user, requestDto));
        log.info("Добавлен новый запрос ID " + request.getId() + " от пользователя ID " + userId);
        return RequestMapper.toRequestServerDto(request);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestServerDto> getUserRequests(Long userId) {
        findUserOrThrowException(userRepo, userId);
        List<Request> userRequests = requestRepo.findAllByRequestorIdOrderByCreatedDesc(userId);
        log.info("Получен список всех запросов пользователя ID " + userId);
        return userRequests.stream()
                .map(RequestMapper::toRequestServerDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestServerDto> getOtherRequests(Long userId, Integer from, Integer size) {
        findUserOrThrowException(userRepo, userId);
        List<Request> requests = requestRepo.findAllByRequestorIdNot(
                userId, PageRequest.of(from / size, size, Sort.by("created").descending()));
        return requests.stream()
                .map(RequestMapper::toRequestServerDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public RequestServerDto getRequest(Long userId, Integer requestId) {
        findUserOrThrowException(userRepo, userId);
        Request request = findRequestOrThrowException(requestRepo, requestId);
        log.info("Получен запрос ID " + requestId);
        RequestServerDto requestServerDto = RequestMapper.toRequestServerDto(request);
        setItems(requestServerDto);
        return requestServerDto;
    }

    private RequestServerDto setItems(RequestServerDto requestDto) {
        List<ItemServerDto> items = itemRepo.findAllByRequestId(requestDto.getId()).stream()
                .map(ItemMapper::toItemServerDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);
        return requestDto;
    }
}
