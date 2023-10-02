package ru.practicum.shareit.exception;

public class UnsupportedStateException extends IllegalArgumentException {
    public UnsupportedStateException(String message) {
        super(message);
    }
}
