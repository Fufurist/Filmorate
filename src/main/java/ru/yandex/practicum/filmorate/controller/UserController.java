package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users;
    public UserController() {
        users = new HashMap<>();
    }

    @PostMapping
    public User create(@RequestBody User user){
        if (user.getEmail() == null || user.getEmail().isBlank()){
            throw new InappropriateInputException("Почта пользователя не может быть пустой");
        }
        if (!user.getEmail().contains("@")){
            throw new InappropriateInputException("Почта пользователя должна содержать @");
        }
        //Опять же в ТЗ нет требования проверки уникальности логина/почты
        if (user.getLogin() == null || user.getLogin().isBlank()){
            throw new InappropriateInputException("Логин не пожет быть пуст");
        }
        if (user.getLogin().contains(" ")){
            throw new InappropriateInputException("Логин не может содержать пробелов");
        }
        if (user.getBirthday().isAfter(Instant.now())){
            throw new InappropriateInputException("Дата рождения не может быть в будущем");
        }

        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user){
        if (!users.containsKey(user.getId())){
            throw new InappropriateInputException("Пользователь с таким ID не найден");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()){
            throw new InappropriateInputException("Почта пользователя не может быть пустой");
        }
        if (!user.getEmail().contains("@")){
            throw new InappropriateInputException("Почта пользователя должна содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()){
            throw new InappropriateInputException("Логин не пожет быть пуст");
        }
        if (user.getLogin().contains(" ")){
            throw new InappropriateInputException("Логин не может содержать пробелов");
        }
        if (user.getBirthday().isAfter(Instant.now())){
            throw new InappropriateInputException("Дата рождения не может быть в будущем");
        }

        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll(){
        return users.values();
    }
}
