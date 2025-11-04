package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestComponent;
import ru.yandex.practicum.filmorate.exceptions.InappropriateInputException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.HashMap;

@TestComponent
public class UserControllerTest {
    private final UserController controller = new UserController(new InMemoryUserStorage(new HashMap<>()),
            new UserService(new InMemoryUserStorage(new HashMap<>())));

    @Test
    public void voidLogin() {
        User user = new User();
        user.setEmail("aaa@");//"golder.ok@mail.com");
        user.setName("");
        user.setLogin(null);
        user.setBirthday(LocalDate.of(2001, 5, 23));
        try {
            controller.create(user);
        } catch (InappropriateInputException e) {
            return;
        } catch (Throwable e) {
            Assertions.fail("Random exception ", e);
        }
        Assertions.fail("Login somehow slipped being empty");
    }

    @Test
    public void emailValidation() {
        User user = new User();
        user.setEmail("aaa@");//"golder.ok@mail.com");
        user.setName("");
        user.setLogin("coller");
        user.setBirthday(LocalDate.of(2001, 5, 23));
        try {
            controller.create(user);
        } catch (Throwable e) {
            Assertions.fail("Unexpected error", e);
        }
        user.setEmail(null);
        try {
            controller.create(user);
        } catch (InappropriateInputException e) {
            try {
                user.setEmail("noDoggo");
                controller.create(user);
            } catch (InappropriateInputException e2) {
                return;
            } catch (Throwable e2) {
                Assertions.fail("Unexpected error", e);
            }
            Assertions.fail("Email without @ slipped");
        } catch (Throwable e) {
            Assertions.fail("Unexpected error", e);
        }
        Assertions.fail("Blank email slipped");
    }

    @Test
    public void birthDayTooLate() {
        User user = new User();
        user.setEmail("aaa@");//"golder.ok@mail.com");
        user.setName("");
        user.setLogin("coller");
        user.setBirthday(LocalDate.of(2030, 5, 23));
        try {
            controller.create(user);
        } catch (InappropriateInputException e) {
            return;
        } catch (Throwable e) {
            Assertions.fail("Random exception ", e);
        }
        Assertions.fail("Date slipped to be in 2030");
    }
}
