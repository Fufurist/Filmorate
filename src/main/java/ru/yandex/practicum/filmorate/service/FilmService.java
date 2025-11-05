package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage films;
    private final UserService userService;

    public Film add(Film film) {
        return films.add(film);
    }

    public Film update(Film film) {
        getFilmOrThrow(film.getId());
        return films.update(film);
    }

    public Collection<Film> values() {
        return films.values();
    }

    public Film getFilmOrThrow(int filmId) throws ElementNotFoundException {
        // Долго пытался придумать, почему делать такой метод не стоит, но в итоге согласился, что такой способ хорош
        // для кастомных исключений. А использование Optional позволяет сделать проверку и получение данных за один
        // запрос в хранилище, а не за два.
        Optional<Film> film = films.getFilm(filmId);
        if (film.isEmpty()) {
            throw new ElementNotFoundException("Фильма с ID " + filmId + "Не существует");
        }
        return film.get();
    }

    public void addLike(int filmId, int userId) {
        userService.getUserOrThrow(userId);
        Film film = getFilmOrThrow(filmId);
        film.addLike(userId);
        films.update(film);
    }

    public void removeLike(int filmId, int userId) {
        userService.getUserOrThrow(userId);
        Film film = getFilmOrThrow(filmId);
        film.removeLike(userId);
        films.update(film);
    }

    public Collection<Film> getNBest(int count) {
        return films.getNBest(count);
    }
}
