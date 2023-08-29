package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}