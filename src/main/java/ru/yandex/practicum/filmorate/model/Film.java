package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    // Если хранить Значения в Сете, то при сериализаци они потеряют свой порядок из-за того, как работает HashSet
    // Если использовать TreeSet, то спринг не может десереализовать объекты фильмов.
    // Остается только так, и проверять на уникальность вручную
    private Collection<NameIdPair> genres = new ArrayList<>();
    private NameIdPair mpa;
    private Set<Integer> likedBy = new HashSet<>();

    public void addLike(int userId) {
        likedBy.add(userId);
    }

    public void removeLike(int userId) {
        likedBy.remove(userId);
    }
}
