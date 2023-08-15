package ru.practicum.shareit.exception;

public class UnauthorizedEdditingException extends IllegalArgumentException {
    public UnauthorizedEdditingException(String message) {
        super(message);
    }
}
