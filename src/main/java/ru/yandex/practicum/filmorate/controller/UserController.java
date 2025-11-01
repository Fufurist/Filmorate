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

        user.setId(getNextFreeId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь");
        return user;
    }

    private int getNextFreeId() {
        return users.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(0) + 1;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.trace("Обновление пользователя");
        validateUser(user);
        if (!users.containsKey(user.getId())) {
            log.error("Такого пользователя нет");
            throw new InappropriateInputException("Такого пользователя нет");
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
