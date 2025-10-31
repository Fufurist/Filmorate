package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users;

    public UserController() {
        users = new HashMap<>();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.trace("Создать нового пользователя");
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
            log.error("Логин не пожет быть пуст");
            throw new InappropriateInputException("Логин не пожет быть пуст");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Логин не может содержать пробелов");
            throw new InappropriateInputException("Логин не может содержать пробелов");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new InappropriateInputException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }

        user.setId(getNextFreeId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь");
        return user;
    }

    private int getNextFreeId() {
        return users.keySet().stream().max(Integer::compare).orElse(0) + 1;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.trace("Обновление пользователя");
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с таким ID не найден");
            throw new InappropriateInputException("Пользователь с таким ID не найден");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Почта пользователя не может быть пустой");
            throw new InappropriateInputException("Почта пользователя не может быть пустой");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Почта пользователя должна содержать @");
            throw new InappropriateInputException("Почта пользователя должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Логин не пожет быть пуст");
            throw new InappropriateInputException("Логин не пожет быть пуст");
        }
        if (user.getLogin().contains(" ")) {
            log.error("Логин не может содержать пробелов");
            throw new InappropriateInputException("Логин не может содержать пробелов");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new InappropriateInputException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос всех пользователей");
        return users.values();
    }
}
