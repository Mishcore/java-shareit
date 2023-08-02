package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserDaoImpl implements UserDao {
    private long id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(users.get(userId));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailAlreadyExistsException("e-mail уже существует");
        }
        userDto.setId(++id);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        return UserMapper.toUserDto(users.get(userDto.getId()));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        User oldUser = users.get(userId);
        User patchedUser = new User(userId, userDto.getName(), userDto.getEmail());

        if (patchedUser.getName() == null) {
            patchedUser.setName(oldUser.getName());
        }
        if (patchedUser.getEmail() == null) {
            patchedUser.setEmail(oldUser.getEmail());
        } else {
            if (users.values().stream().anyMatch(
                    user -> user.getEmail().equals(patchedUser.getEmail()) && !user.equals(users.get(userId))
            )) {
                throw new EmailAlreadyExistsException("e-mail уже существует");
            }
        }
        validateUser(patchedUser);
        users.replace(userId, patchedUser);
        return UserMapper.toUserDto(patchedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        users.remove(userId);
    }

    private void validateUser(User user) {
        Set<ConstraintViolation<User>> violations =
                Validation.buildDefaultValidatorFactory().getValidator().validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
