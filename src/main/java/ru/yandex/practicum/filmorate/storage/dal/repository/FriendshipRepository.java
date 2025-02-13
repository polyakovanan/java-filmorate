package ru.yandex.practicum.filmorate.storage.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;

@Repository
public class FriendshipRepository extends BaseRepository<Friendship> {
    private static final String INSERT_QUERY = "INSERT INTO friendships (user_id, friend_id, is_accepted) " +
                                                "SELECT ?, ?, (SELECT count(*) FROM likes WHERE user_id = ? AND film_id = ?) FROM dual";
    private static final String ACCEPT_QUERY = "UPDATE friendships SET is_accepted = true WHERE user_id = ? AND friend_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM friendships WHERE user_id = ? AND film_id = ?";
    private static final String REMOVE_ACCEPT_QUERY = "UPDATE friendships SET is_accepted = false WHERE user_id = ? AND friend_id = ?";

    public FriendshipRepository(JdbcTemplate jdbc, RowMapper<Friendship> mapper) {
        super(jdbc, mapper);
    }

    public Friendship create(Friendship friendship) {
        insert(INSERT_QUERY,
                friendship.getUserId(),
                friendship.getFriendId(),
                friendship.getFriendId(),
                friendship.getUserId());
        update(ACCEPT_QUERY, friendship.getFriendId(), friendship.getUserId());
        return friendship;
    }

    public Friendship remove(Friendship friendship) {
        delete(DELETE_QUERY, friendship.getUserId(), friendship.getFriendId());
        update(REMOVE_ACCEPT_QUERY, friendship.getFriendId(), friendship.getUserId());
        return friendship;
    }
}
