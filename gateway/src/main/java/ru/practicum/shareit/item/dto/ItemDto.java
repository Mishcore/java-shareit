package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnPatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static ru.practicum.shareit.Constants.STRING_VALIDATION_REGEX;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = STRING_VALIDATION_REGEX, groups = {OnCreate.class, OnPatch.class})
    private String name;

    @NotNull(groups = OnCreate.class)
    @Pattern(regexp = STRING_VALIDATION_REGEX, groups = {OnCreate.class, OnPatch.class})
    private String description;

    @NotNull(groups = OnCreate.class)
    private Boolean available;

    private Integer requestId;
}
