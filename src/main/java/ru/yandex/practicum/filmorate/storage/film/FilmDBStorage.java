package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.NameIdPair;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private NameIdPair generateGenre(int i) {
        NameIdPair ret = new NameIdPair();
        ret.setId(i);
        ret.setName(jdbcTemplate.queryForObject("SELECT name FROM genre WHERE id = ?",
                String.class, i));
        return ret;
    }

    private NameIdPair generateMpa(int i) {
        NameIdPair ret = new NameIdPair();
        ret.setId(i);
        ret.setName(jdbcTemplate.queryForObject("SELECT name FROM rating WHERE id = ?",
                String.class, i));
        return ret;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setMpa(generateMpa(resultSet.getInt("rating_id")));
        return film;
    }

    private void putFilmGenres(int filmId, Collection<NameIdPair> genresIds) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", filmId);
        if (!genresIds.isEmpty()) {
            StringBuilder insertString = new StringBuilder("INSERT INTO films_genres(film_id, genre_id) VALUES");
            boolean flag = false;
            for (NameIdPair i : genresIds) {
                if (flag) insertString.append(",");
                else flag = true;
                insertString.append("(").append(filmId).append(", ").append(i.getId()).append(")");
            }
            jdbcTemplate.update(insertString.toString());
        }
    }

    private Collection<NameIdPair> getFilmGenres(int filmId) {
        return jdbcTemplate.queryForList("SELECT genre_id FROM films_genres WHERE film_id = ? ORDER BY genre_id",
                        int.class, filmId)
                .stream()
                .map(this::generateGenre)
                .toList();
    }

    private void putFilmLikes(int filmId, Collection<Integer> userIds) {
        //проверка на достоверность лайков спрятана в сервисе
        jdbcTemplate.update("DELETE FROM films_likes WHERE film_id = ?", filmId);
        if (!userIds.isEmpty()) {
            StringBuilder insertString = new StringBuilder("INSERT INTO films_likes(film_id, user_id) VALUES");
            boolean flag = false;
            for (int i : userIds) {
                if (flag) insertString.append(",");
                else flag = true;
                insertString.append("(").append(filmId).append(", ").append(i).append(")");
            }
            jdbcTemplate.update(insertString.toString());
        }
    }

    private Collection<Integer> getFilmLikes(int filmId) {
        return Optional.of(jdbcTemplate.queryForList("SELECT user_id FROM films_likes WHERE film_id = ? " +
                        "ORDER BY user_id",
                int.class, filmId)).orElse(new ArrayList<>());
    }

    private Film fillGenresLikes(Film film) {
        if (film != null) {
            film.setGenres(getFilmGenres(film.getId()));
            Collection<Integer> likes = getFilmLikes(film.getId());
            if (!likes.isEmpty()) film.setLikedBy(new HashSet<>(likes));
        }
        return film;
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> insertValue = new HashMap<>();
        insertValue.put("name", film.getName());
        insertValue.put("description", film.getDescription());
        insertValue.put("release_date", Date.valueOf(film.getReleaseDate()));
        insertValue.put("duration", film.getDuration());
        insertValue.put("rating_id", film.getMpa().getId());
        int filmId = simpleJdbcInsert.executeAndReturnKey(insertValue).intValue();
        putFilmGenres(filmId, film.getGenres());
        putFilmLikes(filmId, film.getLikedBy());

        //Идея жалуется, что returnFilm Nullable, но я-то знаю, что не выбросив SQL-ошибку поле не вернётся пустым
        Film returnFilm = jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?",
                this::mapRowToFilm, filmId);
        returnFilm = fillGenresLikes(returnFilm);
        return returnFilm;
    }

    @Override
    public Film update(Film film) {
        String updateQuery = "UPDATE films SET name = ?, description = ?, " +
                "   release_date = ?, duration = ?, rating_id = ? " +
                "WHERE id = ?";

        // В Н2 нельзя применять ON CONFLICT из-за отличающегося синтаксиса, так что буду так обновлять
        jdbcTemplate.update(updateQuery, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().getId(), film.getId());
        putFilmGenres(film.getId(), film.getGenres());
        putFilmLikes(film.getId(), film.getLikedBy());

        Film returnFilm = jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?",
                this::mapRowToFilm, film.getId());
        // Как и тут
        returnFilm = fillGenresLikes(returnFilm);
        return returnFilm;
    }

    @Override
    public Collection<Film> values() {
        String queryStr = "SELECT * FROM films";
        // Хотел обернуть в optional, чтобы не возвращалось пустое множество, а не null, но видимо такой проблемы нет
        return jdbcTemplate.query(queryStr, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        String queryStr = "SELECT * FROM films WHERE id = ?";
        Optional<Film> film;
        try {
            film = Optional.ofNullable(jdbcTemplate.queryForObject(queryStr, this::mapRowToFilm, filmId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        film.ifPresent(this::fillGenresLikes);
        return film;
    }

    @Override
    public Collection<Film> getNBest(int count) {
        String queryStr = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM films f " +
                "INNER JOIN films_likes fl ON f.id = fl.film_id " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "ORDER BY COUNT(fl.user_id) DESC " +
                "LIMIT ? ";
        return jdbcTemplate.query(queryStr, this::mapRowToFilm, count);
    }
}
