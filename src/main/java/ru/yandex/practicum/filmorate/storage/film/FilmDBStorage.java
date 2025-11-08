package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Primary
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setRating(resultSet.getInt("rating_id"));
        return film;
    }

    private void putFilmGenres(int filmId, Collection<Integer> genresIds) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", filmId);
        StringBuilder insertString = new StringBuilder("INSERT INTO films_genres(film_id, genre_id) VALUES");
        boolean flag = false;
        for (int i : genresIds) {
            if (flag) insertString.append(",");
            else flag = true;
            insertString.append("(").append(filmId).append(", ").append(i).append(")");
        }
        jdbcTemplate.update(insertString.toString());
    }

    private Collection<Integer> getFilmGenres(int filmId) {
        return Optional.of(jdbcTemplate.queryForList("SELECT genre_id FROM films_genres WHERE film_id = ?",
                int.class, filmId)).orElse(new ArrayList<>());
    }

    private void putFilmLikes(int filmId, Collection<Integer> userIds) {
        //проверка на достоверность лайков спрятана в сервисе
        jdbcTemplate.update("DELETE FROM films_likes WHERE film_id = ?", filmId);
        StringBuilder insertString = new StringBuilder("INSERT INTO films_likes(film_id, user_id) VALUES");
        boolean flag = false;
        for (int i : userIds) {
            if (flag) insertString.append(",");
            else flag = true;
            insertString.append("(").append(filmId).append(", ").append(i).append(")");
        }
        jdbcTemplate.update(insertString.toString());
    }

    private Collection<Integer> getFilmLikes(int filmId) {
        return Optional.of(jdbcTemplate.queryForList("SELECT user_id FROM films_likes WHERE film_id = ?",
                int.class, filmId)).orElse(new ArrayList<>());
    }

    @Override
    public Film add(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> insertValue = new HashMap<>();
        insertValue.put("name", film.getName());
        insertValue.put("description", film.getName());
        insertValue.put("release_date", Date.valueOf(film.getReleaseDate()));
        insertValue.put("duration", film.getDuration());
        insertValue.put("rating_id", film.getRating());
        int filmId = simpleJdbcInsert.executeAndReturnKey(insertValue).intValue();
        if (!film.getGenres().isEmpty()) putFilmGenres(filmId, film.getGenres());
        if (!film.getLikedBy().isEmpty()) putFilmLikes(filmId, film.getLikedBy());

        //Идея жалуется, что returnFilm Nullable, но я-то знаю, что не выбросив SQL-ошибку поле не вернётся пустым
        Film returnFilm = jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?",
                this::mapRowToFilm, filmId);
        returnFilm.setGenres(getFilmGenres(filmId));
        returnFilm.setLikedBy(new HashSet<>(getFilmLikes(filmId)));
        return returnFilm;
    }

    @Override
    public Film update(Film film) {
        String updateQuery = "UPDATE films SET name = ?, description = ?, " +
                "   release_date = ?, duration = ?, rating_id = ? " +
                "WHERE id = ?";

        // В Н2 нельзя применять ON CONFLICT из-за отличающегося синтаксиса, так что буду так обновлять
        jdbcTemplate.update(updateQuery, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getRating(), film.getId());
        if (!film.getGenres().isEmpty()) putFilmGenres(film.getId(), film.getGenres());
        if (!film.getLikedBy().isEmpty()) putFilmLikes(film.getId(), film.getLikedBy());

        Film returnFilm = jdbcTemplate.queryForObject("SELECT * FROM films WHERE id = ?",
                this::mapRowToFilm, film.getId());
        // Как и тут
        returnFilm.setGenres(getFilmGenres(film.getId()));
        returnFilm.setLikedBy(new HashSet<>(getFilmLikes(film.getId())));
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
        Collection<Integer> genres = getFilmGenres(filmId);
        if (film.isPresent() && !genres.isEmpty()) film.get().setGenres(new HashSet<>(genres));
        Collection<Integer> likes = getFilmLikes(filmId);
        if (film.isPresent() && !likes.isEmpty()) film.get().setLikedBy(new HashSet<>(likes));
        return film;
    }

    @Override
    public Collection<Film> getNBest(int count) {
        String queryStr = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "FROM films AS f " +
                "INNER JOIN films_likes AS fl ON f.id = fl.film_id " +
                "GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id " +
                "ORDER BY COUNT(fl.user_id) " +
                "LIMIT ?) ";
        return jdbcTemplate.query(queryStr, this::mapRowToFilm, count);
    }
}
