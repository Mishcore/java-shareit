package ru.practicum.shareit.exception;

public class EmailAlreadyExistsException extends IllegalArgumentException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
