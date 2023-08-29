package ru.practicum.shareit.exception;

public class UnauthorizedAccessException extends IllegalArgumentException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
