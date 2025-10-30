package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
     private final Map<Integer, Film> films;

    public FilmController() {
        films = new HashMap<>();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new InappropriateInputException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new InappropriateInputException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new InappropriateInputException("Дата релиза должна быть не раньше 28 декабря 1985 года");
        }
        if (!film.getDuration().isPositive()) {
            throw new InappropriateInputException("Длительность фильма должна быть положительной");
        }

        film.setId(getNextFreeId());
        films.put(film.getId(), film);
        return film;
    }

    private int getNextFreeId() {
        return films.keySet().stream().max(Integer::compare).orElse(0) + 1;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new InappropriateInputException("Такого фильма нет в библиотеке");
        }
        //В ТЗ нет пункта о совпадающих названиях фильмов
        if (film.getName() == null || film.getName().isBlank()) {
            throw new InappropriateInputException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new InappropriateInputException("Максимальная длина описания - 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new InappropriateInputException("Дата релиза должна быть не раньше 28 декабря 1985 года");
        }
        if (!film.getDuration().isPositive()) {
            throw new InappropriateInputException("Длительность фильма должна быть положительной");
        }

        //Так было в котграмме. Видимо обновление текущего элемента дешевле вставки нового на это место
        Film oldFilm = films.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        return oldFilm;
    }

    @GetMapping
    public Collection<Film> finAll() {
        return films.values();
    }
}
