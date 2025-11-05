package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmDBStorage implements FilmStorage{
    private final JdbcTemplate jdbcTemplate;

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("name").toLocalDate());
        film.setDuration(resultSet.getInt("birthday"));
        film.setRating(Rating.values()[resultSet.getInt("rating_id")]);
        return film;
    }

    @Override
    public Film add(Film film) {
        String queryStr = "INSERT INTO films(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(queryStr, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRating().ordinal());
        queryStr = "SELECT * FROM films WHERE name = ? AND description = ? AND release_date = ? " +
                "AND duration = ? AND rating_id";
        return jdbcTemplate.query(queryStr, this::mapRowToFilm, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getRating().ordinal()).getFirst();
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public Collection<Film> values() {
        String queryStr = "SELECT * FROM films";
        return jdbcTemplate.query(queryStr, this::mapRowToFilm);
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        String queryStr = "SELECT * FROM films WHERE id = ?";
        return Optional.ofNullable(jdbcTemplate.query(queryStr, this::mapRowToFilm, filmId).getFirst());
    }

    @Override
    public Collection<Film> getNBest(int count) {
        String queryStr = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_id" +
                          "FROM films AS f" +
                          "JOIN films_likes AS fl ON fl.filmid = films.id";
        return jdbcTemplate.query(queryStr, this::mapRowToFilm, count);
    }
}
