package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

@UtilityClass
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
            null,
            userDto.getName(),
            userDto.getEmail()
        );
    }
}
