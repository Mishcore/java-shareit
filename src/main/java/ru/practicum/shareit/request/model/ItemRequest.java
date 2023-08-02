package ru.practicum.shareit.request.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Integer id;

    @NotBlank
    private String description;

    @NotNull
    private Long requestor;
    private LocalDateTime created;
}