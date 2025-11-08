package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Primary
public class UserDBStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        return user;
    }

    private void putUserFriends(int userId, Collection<Integer> friendsIds) {
        jdbcTemplate.update("DELETE FROM friend_ids WHERE user_id = ?", userId);
        StringBuilder insertString = new StringBuilder("INSERT INTO friend_ids(user_id, friend_id) VALUES");
        boolean flag = false;
        for (int i : friendsIds) {
            if (flag) insertString.append(",");
            else flag = true;
            insertString.append("(").append(userId).append(", ").append(i).append(")");
        }
        jdbcTemplate.update(insertString.toString());
    }

    private Collection<Integer> getUserFriends(int userId) {
        return Optional.of(jdbcTemplate.queryForList("SELECT friend_id FROM friend_ids WHERE user_id = ?",
                int.class, userId)).orElse(new ArrayList<>());
    }

    @Override
    public User add(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> insertValue = new HashMap<>();
        insertValue.put("email", user.getEmail());
        insertValue.put("login", user.getLogin());
        insertValue.put("name", user.getName());
        insertValue.put("birthday", Date.valueOf(user.getBirthday()));
        int userId = simpleJdbcInsert.executeAndReturnKey(insertValue).intValue();
        if (!user.getFriendIds().isEmpty()) putUserFriends(userId, user.getFriendIds());

        User returnUser = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?",
                this::mapRowToUser, userId);
        returnUser.setFriendIds(new HashSet<>(getUserFriends(userId)));
        return returnUser;
    }

    @Override
    public User update(User user) {
        String updateQuery = "UPDATE users SET email = ?, login = ?, " +
                "name = ?, birthday = ?" +
                "WHERE id = ?";

        jdbcTemplate.update(updateQuery, user.getEmail(), user.getLogin(), user.getName(),
                Date.valueOf(user.getBirthday()), user.getId());
        if (!user.getFriendIds().isEmpty()) putUserFriends(user.getId(), user.getFriendIds());

        User returnUser = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id = ?",
                this::mapRowToUser, user.getId());
        // Как и тут
        returnUser.setFriendIds(new HashSet<>(getUserFriends(user.getId())));
        return returnUser;
    }

    @Override
    public Collection<User> values() {
        String queryStr = "SELECT * FROM users";
        // Хотел обернуть в optional, чтобы не возвращалось пустое множество, а не null, но видимо такой проблемы нет
        return jdbcTemplate.query(queryStr, this::mapRowToUser);
    }

    @Override
    public Optional<User> getUser(int id) {
        String queryStr = "SELECT * FROM users WHERE id = ?";
        Optional<User> user;
        try {
            user = Optional.ofNullable(jdbcTemplate.queryForObject(queryStr, this::mapRowToUser, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        Collection<Integer> friends = getUserFriends(id);
        if (user.isPresent() && !friends.isEmpty()) user.get().setFriendIds(new HashSet<>(friends));
        return user;
    }
}
