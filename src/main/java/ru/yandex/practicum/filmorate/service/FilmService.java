package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.NameIdPair;
import ru.yandex.practicum.filmorate.storage.ConstantsStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage films;
    private final UserService userService;
    private final ConstantsStorage constantsStorage;

    private void mpaGenreValidation(Film film) {
        if (film.getMpa().getId() > 5 || film.getMpa().getId() < 1) {
            throw new ElementNotFoundException("Не существует рейтинга с id " + film.getMpa().getId());
        }
        Collection<Integer> genreIdList = constantsStorage.getAllGenres()
                .stream()
                .map(NameIdPair::getId)
                .toList();
        for (int i : film.getGenres()
                .stream()
                .map(NameIdPair::getId)
                .toList()) {
            if (!genreIdList.contains(i)) {
                throw new ElementNotFoundException("Не существует рейтинга с id " + i);
            }
        }
    }

    public Film add(Film film) {
        mpaGenreValidation(film);
        return films.add(film);
    }

    public Film update(Film film) {
        mpaGenreValidation(film);
        getFilmOrThrow(film.getId());
        return films.update(film);
    }

    public Collection<Film> values() {
        return films.values();
    }

    public Film getFilmOrThrow(int filmId) throws ElementNotFoundException {
        return films.getFilm(filmId).orElseThrow(() ->
                new ElementNotFoundException("Фильма с ID " + filmId + " Не существует"));
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
