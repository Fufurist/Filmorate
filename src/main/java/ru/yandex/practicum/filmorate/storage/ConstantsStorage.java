package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.NameIdPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        List<String> quer = jdbcTemplate.queryForList("SELECT name FROM genre ORDER BY id", String.class);
        Collection<NameIdPair> ret = new ArrayList<>();
        for (int i = 1; i <= quer.size(); i++) {
            ret.add(new NameIdPair(i, quer.get(i - 1)));
        }
        return ret;
    }

    public Collection<NameIdPair> getAllRatings() {
        List<String> quer = jdbcTemplate.queryForList("SELECT name FROM rating ORDER BY id", String.class);
        Collection<NameIdPair> ret = new ArrayList<>();
        for (int i = 1; i <= quer.size(); i++) {
            ret.add(new NameIdPair(i, quer.get(i - 1)));
        }
        return ret;
    }
}
