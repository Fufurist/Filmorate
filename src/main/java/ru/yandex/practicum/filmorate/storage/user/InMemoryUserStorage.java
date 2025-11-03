package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;
    private int lastId;

    @Override
    public User add(User user) {
        lastId += 1;
        user.setId(lastId);
        return users.put(lastId, user);
    }

    @Override
    public User update(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public boolean containsKey(int key) {
        return users.containsKey(key);
    }

    @Override
    public Collection<User> values() {
        return users.values();
    }
}
