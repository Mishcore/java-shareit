package ru.practicum.shareit.exception;

public class InvalidOperationException extends IllegalArgumentException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
