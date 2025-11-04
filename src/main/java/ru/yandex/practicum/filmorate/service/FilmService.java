package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage films;

    public void addLike(int filmId, int userId) {
        Film film = films.getFilm(filmId);
        film.addLike(userId);
        films.update(film);
    }

    public void removeLike(int filmId, int userId) {
        Film film = films.getFilm(filmId);
        film.removeLike(userId);
        films.update(film);
    }
}
