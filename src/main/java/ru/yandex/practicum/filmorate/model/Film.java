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
    private Set<Integer> likedBy = new HashSet<>();
    private Collection<Integer> genres = new ArrayList<>();
    private int rating;

    public void addLike(int userId) {
        likedBy.add(userId);
    }

    public void removeLike(int userId) {
        likedBy.remove(userId);
    }
}
