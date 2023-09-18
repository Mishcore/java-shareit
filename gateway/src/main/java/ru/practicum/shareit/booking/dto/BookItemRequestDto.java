package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validator.ValidStartEndDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ValidStartEndDate
public class BookItemRequestDto {
	@Positive
	private int itemId;
	private LocalDateTime start;
	private LocalDateTime end;
}