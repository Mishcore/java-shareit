package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userDao.getAllUsers();
        log.info("Получен список всех пользователей");
        return users;
    }

    @Override
    public UserDto getUser(Long userId) {
        UserDto userDto = userDao.getUser(userId);
        log.info("Получен пользоатель ID " + userId);
        return userDto;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        UserDto addedUserDto = userDao.addUser(userDto);
        log.info("Добавлен новый пользователь ID " + addedUserDto.getId());
        return addedUserDto;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        UserDto updatedUser = userDao.updateUser(userId, userDto);
        log.info("Обновлены данные пользователя ID " + userId);
        return updatedUser;
    }

    @Override
    public void deleteUser(Long userId) {
        userDao.deleteUser(userId);
        log.info("Удалён пользователь ID " + userId);
    }
}