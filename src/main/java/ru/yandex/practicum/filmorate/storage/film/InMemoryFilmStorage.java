package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLikesComparator;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int lastId;

    @Override
    public Film add(Film film) {
        lastId += 1;
        film.setId(lastId);
        films.put(lastId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> values() {
        return films.values();
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    // Поместил сюда, т.к. потом черед БД будет более удобная сортировка и сборка
    public Collection<Film> getNBest(int count) {
        return films.values()
                .stream()
                .sorted(new FilmLikesComparator())
                .limit(count)
                .toList();
    }
}
