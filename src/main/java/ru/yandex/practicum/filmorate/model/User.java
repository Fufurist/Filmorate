package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friendIds;

    public void addFriendId(int friendId) {
        friendIds.add(friendId);
    }

    public void removeFriendId(int id) {
        friendIds.remove(id);
    }
}
