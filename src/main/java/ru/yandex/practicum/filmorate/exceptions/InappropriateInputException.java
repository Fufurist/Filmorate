package ru.yandex.practicum.filmorate.exceptions;

public class InappropriateInputException extends RuntimeException {
    public InappropriateInputException(String message) {
        super(message);
    }
}
