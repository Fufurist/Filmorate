package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage films;
    private final FilmService filmService;
    private final UserStorage users;
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
            throw new ElementNotFoundException("Такого фильма нет");
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

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        if (!films.containsKey(id)) {
            throw new ElementNotFoundException("Такого фильма не существует");
        }
        if (!users.containsKey(userId)) {
            throw new ElementNotFoundException("Такого пользователя не существует");
        }

        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        if (!films.containsKey(id)) {
            throw new ElementNotFoundException("Такого фильма не существует");
        }
        if (!users.containsKey(userId)) {
            throw new ElementNotFoundException("Такого пользователя не существует");
        }

        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> addLike(@RequestParam(required = false) Integer count) {

        return films.getNBest((count == null) ? 10 : count);
    }
}
