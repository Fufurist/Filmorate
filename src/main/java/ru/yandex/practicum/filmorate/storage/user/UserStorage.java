package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User add(User user);

    public User update(User user);

    public boolean containsKey(int key);

    public Collection<User> values();

    public User getUser(int id);
}
