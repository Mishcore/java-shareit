package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnPatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static ru.practicum.shareit.Constants.STRING_VALIDATION_REGEX;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = STRING_VALIDATION_REGEX, groups = {OnCreate.class, OnPatch.class})
    private String name;

    @NotNull(groups = OnCreate.class)
    @Email(groups = {OnCreate.class, OnPatch.class})
    private String email;
}
