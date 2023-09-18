package ru.practicum.shareit.exception;

public class ItemNotAvailableException extends IllegalArgumentException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
