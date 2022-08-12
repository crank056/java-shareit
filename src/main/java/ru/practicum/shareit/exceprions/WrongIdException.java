package ru.practicum.shareit.exceprions;

public class WrongIdException extends Exception {
    public WrongIdException(String message) {
        super(message);
    }
}