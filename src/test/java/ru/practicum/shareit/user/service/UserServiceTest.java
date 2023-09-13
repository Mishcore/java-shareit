package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private final User user1 = new User(1L, "test1", "test@mail.ru");
    private final User user2 = new User(2L, "test2", "test@mail.com");

    @Test
    void getAllUsersWhenUsersExistThenUserDtoList() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(2, userDtoList.size());
        assertEquals(user1.getId(), userDtoList.get(0).getId());
        assertEquals(user1.getName(), userDtoList.get(0).getName());
        assertEquals(user1.getEmail(), userDtoList.get(0).getEmail());
        assertEquals(user2.getId(), userDtoList.get(1).getId());
        assertEquals(user2.getName(), userDtoList.get(1).getName());
        assertEquals(user2.getEmail(), userDtoList.get(1).getEmail());
    }

    @Test
    void getAllUsersWhenNoUsersThenEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserDto> userDtoList = assertDoesNotThrow(() -> userService.getAllUsers());
        assertTrue(userDtoList.isEmpty());
    }

    @Test
    void getUserWhenUserExistsThenUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDto actualUser = userService.getUser(1L);

        assertEquals(user1.getId(), actualUser.getId());
        assertEquals(user1.getName(), actualUser.getName());
        assertEquals(user1.getEmail(), actualUser.getEmail());
    }

    @Test
    void getUserWhenUserNotFoundThenException() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUser(3L));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void addUserWhenValidUserDtoThenPersistUserAndReturnUserDto() {
        UserDto userDto = new UserDto(null, "test name", "test@ya.ru");
        when(userRepository.save(any())).thenReturn(new User(3L, "test name", "test@ya.ru"));
        UserDto actualUser = userService.addUser(userDto);
        assertEquals(3L, actualUser.getId());
        assertEquals(userDto.getName(), actualUser.getName());
        assertEquals(userDto.getEmail(), actualUser.getEmail());
    }

    @Test
    void addUserWhenEmailConflictThenException() {
        UserDto userDto = new UserDto(null, "test name", "test@mail.ru");
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        EmailAlreadyExistsException exception =
                assertThrows(EmailAlreadyExistsException.class, () -> userService.addUser(userDto));
        assertEquals("Уже существует пользователь с таким e-mail", exception.getMessage());
    }

    @Test
    void updateUserWhenValidUserDtoWithAllNewFieldsThenUpdateUserAndReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDto userDto = new UserDto(null, "test name", "test@ya.ru");
        when(userRepository.save(any())).thenReturn(new User(1L, "test name", "test@ya.ru"));
        UserDto actualUser = userService.updateUser(1L, userDto);

        assertEquals(user1.getId(), actualUser.getId());
        assertEquals(userDto.getName(), actualUser.getName());
        assertEquals(userDto.getEmail(), actualUser.getEmail());
    }

    @Test
    void updateUserWhenValidUserDtoWithNewNameThenUpdateUserAndReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDto userDto = new UserDto(null, "test name", "test@mail.ru");
        when(userRepository.save(any())).thenReturn(new User(1L, "test name", "test@mail.ru"));
        UserDto actualUser = userService.updateUser(1L, userDto);

        assertEquals(user1.getId(), actualUser.getId());
        assertEquals(userDto.getName(), actualUser.getName());
        assertEquals(user1.getEmail(), actualUser.getEmail());
    }

    @Test
    void updateUserWhenValidUserDtoWithNewEmailThenUpdateUserAndReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDto userDto = new UserDto(null, "test1", "test@ya.ru");
        when(userRepository.save(any())).thenReturn(new User(1L, "test1", "test@ya.ru"));
        UserDto actualUser = userService.updateUser(1L, userDto);

        assertEquals(user1.getId(), actualUser.getId());
        assertEquals(user1.getName(), actualUser.getName());
        assertEquals(userDto.getEmail(), actualUser.getEmail());
    }

    @Test
    void updateUserWhenUserNotFoundThenException() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        UserDto userDto = new UserDto(null, "test1", "test@ya.ru");

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> userService.updateUser(3L, userDto));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserWhenInvalidUserDtoThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserDto userDto = new UserDto(null, "", "");

        assertThrows(ConstraintViolationException.class, () -> userService.updateUser(1L, userDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUserWhenEmailConflictThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);
        UserDto userDto = new UserDto(null, "test1", "test@mail.ru");

        EmailAlreadyExistsException exception =
                assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(1L, userDto));
        assertEquals("Уже существует пользователь с таким e-mail", exception.getMessage());
    }

    @Test
    void deleteUserWhenUserExistsThenDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserWhenUserNotFoundThenException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository, never()).deleteById(1L);
    }
}