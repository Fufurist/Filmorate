package ru.yandex.practicum.filmorate.exceptions;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String description;
}
