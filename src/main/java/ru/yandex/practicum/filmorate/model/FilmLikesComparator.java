package ru.yandex.practicum.filmorate.model;

import java.util.Comparator;

public class FilmLikesComparator implements Comparator<Film> {
    @Override
    public int compare(Film film1, Film film2) {
        //Тут нужен обратный порядок сортировки
        return Integer.compare(film2.getLikesBy().size(), film1.getLikesBy().size());
    }
}
