package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<IdClass> genres = new HashSet<>();
    private IdClass mpa;
    private Set<Integer> likedBy = new HashSet<>();

    public void addLike(int userId) {
        likedBy.add(userId);
    }

    public void removeLike(int userId) {
        likedBy.remove(userId);
    }
}
