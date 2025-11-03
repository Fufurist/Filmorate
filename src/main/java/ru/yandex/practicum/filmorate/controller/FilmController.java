package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage films;
    private static final LocalDate CINEMA_BIRTH = LocalDate.of(1895, 12, 28);

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new InappropriateInputException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Максимальная длина описания - 200 символов");
            throw new InappropriateInputException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.error("Дата выхода должна быть известна");
            throw new InappropriateInputException("Дата выхода должна быть известна");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTH)) {
            log.error("Дата выхода должна быть не раньше 28 декабря 1985 года");
            throw new InappropriateInputException("Дата выхода должна быть не раньше 28 декабря 1985 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Длительность фильма должна быть положительной");
            throw new InappropriateInputException("Длительность фильма должна быть положительной");
        }
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.trace("Добавление фильма");
        validateFilm(film);

        films.add(film);
        log.info("Добавлен новый фильм");
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.trace("Обновление фильма ");
        validateFilm(film);
        if (!films.containsKey(film.getId())) {
            log.error("Такого фильма нет");
            throw new InappropriateInputException("Такого фильма нет");
        }

        film = films.update(film);
        log.info("Фильм успешно изменён");
        return film;
    }

    @GetMapping
    public Collection<Film> finAll() {
        log.info("Запрос всех фильмов");
        return films.values();
    }
}
