package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(InappropriateInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInappropriateInput(InappropriateInputException e) {
        return new ErrorResponse("InappropriateInputException", e.getMessage());
    }

    @ExceptionHandler(ElementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleElementNotFound(ElementNotFoundException e) {
        return new ErrorResponse("ElementNotFoundException", e.getMessage());
    }
}
