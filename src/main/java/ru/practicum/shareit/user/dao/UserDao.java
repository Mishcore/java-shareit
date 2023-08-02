package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserDao {

    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);

    UserDto addUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}
