package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.request.model.RequestServerDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class RequestMapper {
    public static Request toRequest(User user, RequestClientDto requestDto) {
        return new Request(
                null,
                requestDto.getDescription(),
                user,
                LocalDateTime.now()
        );
    }

    public static RequestServerDto toRequestServerDto(Request request) {
        return new RequestServerDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                null
        );
    }
}
