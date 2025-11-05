package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserStorage users;

    public User add(User user) {
        return users.add(user);
    }

    public User update(User user) {
        return users.update(user);
    }

    public Collection<User> values() {
        return users.values();
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.addFriendId(friendId);
        friend.addFriendId(userId);

        users.update(user);
        users.update(friend);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        //Проверку на наличие в списке вынесем к остальным валидациям

        user.removeFriendId(friendId);
        friend.removeFriendId(userId);

        users.update(user);
        users.update(friend);
    }

    public Collection<User> findCommonFriends(int user1, int user2) {
        Set<Integer> user1Friends = getUserOrThrow(user1).getFriendIds();
        Collection<User> returnCollection = new ArrayList<>();
        for (Integer i : getUserOrThrow(user2).getFriendIds()) {
            if (user1Friends.contains(i)) returnCollection.add(getUserOrThrow(i));
        }
        return returnCollection;
    }

    public User getUserOrThrow(int userId) {
        Optional<User> user = users.getUser(userId);
        if (user.isEmpty()) {
            throw new ElementNotFoundException("Пользователя с ID " + userId + "Не существует");
        }
        return user.get();
    }

    public Collection<User> getUserFriends(int id) {
        return getUserOrThrow(id).getFriendIds()
                .stream()
                .map(this::getUserOrThrow)
                .toList();
    }
}
