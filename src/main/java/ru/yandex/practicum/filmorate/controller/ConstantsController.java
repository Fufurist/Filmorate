package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.NameIdPair;
import ru.yandex.practicum.filmorate.storage.ConstantsStorage;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class ConstantsController {
    private final ConstantsStorage constantsStorage;

    @GetMapping("/genres")
    public Collection<NameIdPair> getAllGenres() {
        return constantsStorage.getAllGenres();
    }

    @GetMapping("/mpa")
    public Collection<NameIdPair> getAllRatings() {
        return constantsStorage.getAllRatings();
    }


    @GetMapping("/genres/{id}")
    public NameIdPair getGenre(@PathVariable int id) {
        return Optional.ofNullable(constantsStorage.getGenre(id))
                .orElseThrow(() -> new ElementNotFoundException("Не существует жанра с id " + id));
    }

    @GetMapping("/mpa/{id}")
    public NameIdPair getRating(@PathVariable int id) {
        return Optional.ofNullable(constantsStorage.getRating(id))
                .orElseThrow(() -> new ElementNotFoundException("Не существует рейтинга с id " + id));
    }
}
