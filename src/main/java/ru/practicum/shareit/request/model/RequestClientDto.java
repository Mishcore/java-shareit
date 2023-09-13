package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RequestClientDto {

    @NotBlank
    private String description;

    @JsonCreator
    public RequestClientDto(String description) {
        this.description = description;
    }
}