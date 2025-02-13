package ru.yandex.practicum.filmorate.storage.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Repository
public class LikeRepository extends BaseRepository<Like> {
    private static final String INSERT_QUERY = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";

    public LikeRepository(JdbcTemplate jdbc, RowMapper<Like> mapper) {
        super(jdbc, mapper);
    }

    public Like create(Like like) {
        insert(INSERT_QUERY, like.getUserId(), like.getFilmId());
        return like;
    }

    public Like remove(Like like) {
        delete(DELETE_QUERY, like.getUserId(), like.getFilmId());
        return like;
    }
}
