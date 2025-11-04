package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLikesComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        films.put(lastId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean containsKey(int key) {
        return films.containsKey(key);
    }

    @Override
    public Collection<Film> values() {
        return films.values();
    }

    @Override
    public Film getFilm(int filmId) {
        return films.get(filmId);
    }

    // Поместил сюда, т.к. потом черед БД будет более удобная сортировка и сборка
    public Collection<Film> getNBest(int count) {
        List<Film> sortedFilms = films.values()
                .stream()
                .sorted(new FilmLikesComparator())
                .toList();
        // Не нашел коллектора, который собирал бы только первые N элементов. Скорее всего просто плохо искал
        List<Film> firstN = new ArrayList<>();
        for (int i = 0; i < Integer.min(count, sortedFilms.size()); i++) {
            firstN.add(sortedFilms.get(i));
        }
        return firstN;
    }
}
