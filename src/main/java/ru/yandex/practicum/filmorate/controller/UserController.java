package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Почта пользователя не может быть пустой");
            throw new InappropriateInputException("Почта пользователя не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Почта пользователя должна содержать @");
            throw new InappropriateInputException("Почта пользователя должна содержать @");
        }
        //Опять же в ТЗ нет требования проверки уникальности логина/почты
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Логин не может быть пуст");
            throw new InappropriateInputException("Логин не может быть пуст");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Логин не может содержать пробелов");
            throw new InappropriateInputException("Логин не может содержать пробелов");
        }
        if (user.getBirthday() == null) {
            log.error("Я запрещаю вам не иметь даты рождения");
            throw new InappropriateInputException("Я запрещаю вам не иметь даты рождения");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new InappropriateInputException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.trace("Создать нового пользователя");
        validateUser(user);

        user = userService.add(user);
        log.info("Создан новый пользователь");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.trace("Обновление пользователя");
        validateUser(user);

        user = userService.update(user);
        log.info("Пользователь обновлен");
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос всех пользователей");
        return userService.values();
    }

    private void commonFriendIdValidation(int id, int friendId) {
        if (id == friendId) {
            throw new InappropriateInputException("Пользователь не может быть другом себе");
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        commonFriendIdValidation(id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        commonFriendIdValidation(id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        return userService.getUserFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        commonFriendIdValidation(id, otherId);
        return userService.findCommonFriends(id, otherId);
    }
}
