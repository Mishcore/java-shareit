package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.ItemServerDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RequestServerDto {
    private Integer id;
    private String description;
    private LocalDateTime created;
    private List<ItemServerDto> items;
}
