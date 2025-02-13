package ru.yandex.practicum.filmorate.storage.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String INSERT_QUERY = "INSERT INTO users (email, login, name, birthdate) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthdate = ? WHERE id = ?";

    private static final String FIND_FRIENDS_BY_ID_QUERY = "SELECT * FROM users WHERE id IN (SELECT friend_id FROM friendships WHERE user_id = ?)";
    private static final String FIND_COMMON_FRIENDS_QUERY = "SELECT * FROM users " +
                                                            "WHERE id IN (SELECT friend_id FROM friendships WHERE user_id = ?)" +
                                                            "AND id IN (SELECT friend_id FROM friendships WHERE user_id = ?)";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Optional<User> findById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public User create(User user) {
        long id = insertWithGeneratedId(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(Instant.from(user.getBirthday()))
        );
        user.setId(id);
        return user;
    }

    public User update(User user) {
        update(UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.from(Instant.from(user.getBirthday()))
        );
        return user;
    }

    public List<User> findFriendsById(Long userId) {
        return findMany(FIND_FRIENDS_BY_ID_QUERY, userId);
    }

    public List<User> findCommonFriends(Long userId, Long friendId) {
        return findMany(FIND_COMMON_FRIENDS_QUERY, userId, friendId);
    }

}
