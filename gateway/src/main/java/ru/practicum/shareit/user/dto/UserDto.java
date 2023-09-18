package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
}
