package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
     private final Map<Integer, Film> films;

    public FilmController() {
        films = new HashMap<>();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.trace("Добавление фильма ");
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new InappropriateInputException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Максимальная длина описания - 200 символов");
            throw new InappropriateInputException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза должна быть не раньше 28 декабря 1985 года");
            throw new InappropriateInputException("Дата релиза должна быть не раньше 28 декабря 1985 года");
        }
        if (!film.getDuration().isPositive()) {
            log.error("Длительность фильма должна быть положительной");
            throw new InappropriateInputException("Длительность фильма должна быть положительной");
        }

        film.setId(getNextFreeId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм");
        return film;
    }

    private int getNextFreeId() {
        return films.keySet().stream().max(Integer::compare).orElse(0) + 1;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.trace("Обновление фильма ");
        if (!films.containsKey(film.getId())) {
            log.error("Такого фильма нет в библиотеке");
            throw new InappropriateInputException("Такого фильма нет в библиотеке");
        }
        //В ТЗ нет пункта о совпадающих названиях фильмов
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new InappropriateInputException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.error("Максимальная длина описания - 200 символов");
            throw new InappropriateInputException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза должна быть не раньше 28 декабря 1985 года");
            throw new InappropriateInputException("Дата релиза должна быть не раньше 28 декабря 1985 года");
        }
        if (!film.getDuration().isPositive()) {
            log.error("Длительность фильма должна быть положительной");
            throw new InappropriateInputException("Длительность фильма должна быть положительной");
        }

        //Так было в котграмме. Видимо обновление текущего элемента дешевле вставки нового на это место
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        log.info("Фильм успешно изменён");
        return oldFilm;
    }

    @GetMapping
    public Collection<Film> finAll() {
        log.info("Запрос всех фильмов");
        return films.values();
    }
}
