package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Film add(Film film);

    public Film update(Film film);

    public boolean containsKey(int key);

    public Collection<Film> values();

    public Film getFilm(int filmId);

    public Collection<Film> getNBest(int count);
}
