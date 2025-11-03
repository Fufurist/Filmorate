package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films;
    private int lastId;

    @Override
    public Film add(Film film) {
        lastId += 1;
        film.setId(lastId);
        return films.put(lastId, film);
    }

    @Override
    public Film update(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public boolean containsKey(int key) {
        return films.containsKey(key);
    }

    @Override
    public Collection<Film> values() {
        return films.values();
    }
}
