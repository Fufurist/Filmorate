package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserService {
    private final UserStorage users;

    public void addFriend(int userId, int friendId) {
        User user = users.getUser(userId);
        User friend = users.getUser(friendId);
        //Проверку на наличие в списке вынесем к остальным валидациям

        user.addFriendId(friendId);
        friend.addFriendId(userId);

        users.update(user);
        users.update(friend);
    }

    public void removeFriend(int userId, int friendId) {
        User user = users.getUser(userId);
        User friend = users.getUser(friendId);
        //Проверку на наличие в списке вынесем к остальным валидациям

        user.removeFriendId(friendId);
        friend.removeFriendId(userId);

        users.update(user);
        users.update(friend);
    }

    public Collection<User> getFriendslist(int userId) {
        return users.getUser(userId).getFriendIds()
                .stream()
                .map(users::getUser)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(int user1, int user2) {
        Set<Integer> user1Friends = users.getUser(user1).getFriendIds();
        Collection<User> returnCollection = new ArrayList<>();
        for (Integer i : users.getUser(user2).getFriendIds()) {
            //только 1 проход, а contains для Set операция не сложная
            if (user1Friends.contains(i)) returnCollection.add(users.getUser(i));
        }
        return returnCollection;
    }
}
