package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class FilmLikesComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        return Integer.compare(film1.getLikesBy().size(), film2.getLikesBy().size());
    }
}
