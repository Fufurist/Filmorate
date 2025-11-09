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
            NameIdPair ret = new NameIdPair();
            ret.setId(id);
            ret.setName(jdbcTemplate.queryForObject("SELECT name FROM genre WHERE id = ?",
                    String.class, id));
            return ret;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public NameIdPair getRating(int id) {
        try {
            NameIdPair ret = new NameIdPair();
            ret.setId(id);
            ret.setName(jdbcTemplate.queryForObject("SELECT name FROM rating WHERE id = ?",
                    String.class, id));
            return ret;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Collection<NameIdPair> getAllGenres() {
        List<String> quer = jdbcTemplate.queryForList("SELECT name FROM genre ORDER BY id", String.class);
        Collection<NameIdPair> ret = new ArrayList<>();
        for (int i = 1; i <= quer.size(); i++) {
            NameIdPair genre = new NameIdPair();
            genre.setName(quer.get(i - 1));
            genre.setId(i);
            ret.add(genre);
        }
        return ret;
    }

    public Collection<NameIdPair> getAllRatings() {
        List<String> quer = jdbcTemplate.queryForList("SELECT name FROM rating ORDER BY id", String.class);
        Collection<NameIdPair> ret = new ArrayList<>();
        for (int i = 1; i <= quer.size(); i++) {
            NameIdPair rating = new NameIdPair();
            rating.setName(quer.get(i - 1));
            rating.setId(i);
            ret.add(rating);
        }
        return ret;
    }
}
