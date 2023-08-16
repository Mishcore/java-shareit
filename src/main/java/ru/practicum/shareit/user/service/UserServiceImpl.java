package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepo.findAll();
        log.info("Получен список всех пользователей");
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long userId) {
        User user = findUserOrThrowException(userId);
        log.info("Получен пользоатель ID " + userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        try {
            User user = userRepo.save(UserMapper.toUser(userDto));
            log.info("Добавлен новый пользователь ID " + user.getId());
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Уже существует пользователь с таким e-mail");
        }
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userFromRepo = findUserOrThrowException(userId);

        if (userDto.getName() != null) {
            userFromRepo.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userFromRepo.setEmail(userDto.getEmail());
        }
        try {
            User user = userRepo.save(userFromRepo);
            log.info("Обновлены данные пользователя ID " + userId);
            return UserMapper.toUserDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException("Уже существует пользователь с таким e-mail");
        }
    }

    @Override
    public void deleteUser(Long userId) {
        User user = findUserOrThrowException(userId);
        userRepo.deleteById(user.getId());
        log.info("Удалён пользователь ID " + user.getId());
    }

    private User findUserOrThrowException(Long userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return userOpt.get();
    }
}