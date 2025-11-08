package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.NameIdPair;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class ConstantsStorage {
    private final JdbcTemplate jdbcTemplate;

    public NameIdPair getGenre(int id) {
        try {
            return new NameIdPair(id, jdbcTemplate.queryForObject("SELECT name FROM genre WHERE id = ?",
                    String.class, id));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public NameIdPair getRating(int id) {
        try {
            return new NameIdPair(id, jdbcTemplate.queryForObject("SELECT name FROM rating WHERE id = ?",
                    String.class, id));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<NameIdPair> getAllGenres() {
        return jdbcTemplate.queryForList("SELECT * FROM genre", NameIdPair.class);
    }

    public Collection<NameIdPair> getAllRatings() {
        return jdbcTemplate.queryForList("SELECT * FROM rating", NameIdPair.class);
    }
}
