package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

import static ru.practicum.shareit.Constants.EMAIL_VALIDATION_REGEX;

@Slf4j
@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Запрос на создание пользователя {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody UserDto userDto) {
        validateUserDto(userDto);
        log.info("Запрос на обновление пользователя userId={}", userId);
        return userClient.updateUser(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Запрос на получение пользователя userId={}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Запрос на удаление пользователя userId={}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto.getName() != null && userDto.getName().isBlank()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (userDto.getEmail() != null && !userDto.getEmail().matches(EMAIL_VALIDATION_REGEX)) {
            throw new IllegalArgumentException("Некорректный email");
        }
    }
}
