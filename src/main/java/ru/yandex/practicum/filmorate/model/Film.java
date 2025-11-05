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
    private Set<Integer> likesBy = new HashSet<>();

    public void addLike(int userId) {
        likesBy.add(userId);
    }

    public void removeLike(int userId) {
        likesBy.remove(userId);
    }
}
