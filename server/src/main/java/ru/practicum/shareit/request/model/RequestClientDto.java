package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class RequestClientDto {
    private String description;

    @JsonCreator
    public RequestClientDto(String description) {
        this.description = description;
    }
}