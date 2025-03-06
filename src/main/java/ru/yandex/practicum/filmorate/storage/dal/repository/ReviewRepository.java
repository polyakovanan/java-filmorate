package ru.yandex.practicum.filmorate.storage.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE review_id = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM reviews WHERE film_id = ? OR ? IS NULL ORDER BY useful DESC LIMIT ?";
    private static final String INSERT_QUERY = "INSERT INTO reviews (content, is_positive, user_id, film_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE review_id = ?";
    private static final String ADD_REACTION_QUERY = "INSERT INTO review_reactions (review_id, user_id, is_positive) VALUES (?, ?, ?)";
    private static final String REMOVE_REACTION_QUERY = "DELETE FROM review_reactions WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_USEFUL_QUERY = "UPDATE reviews SET useful = (SELECT COUNT(CASE WHEN is_positive THEN 1 END) - COUNT(CASE WHEN NOT is_positive THEN 1 END) FROM review_reactions WHERE review_id = ?) WHERE review_id = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public List<Review> findAll(Long filmId, int count) {
        return findMany(FIND_ALL_QUERY, filmId, filmId, count);
    }

    public Optional<Review> findById(long reviewId) {
        return findOne(FIND_BY_ID_QUERY, reviewId);
    }

    public Review create(Review review) {
        long id = insertWithGeneratedId(INSERT_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        review.setReviewId(id);
        review.setUseful(0);
        return review;
    }

    public Review update(Review review) {
        update(UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return review;
    }

    public void delete(long reviewId) {
        delete(DELETE_QUERY, reviewId);
    }

    public void addReaction(long reviewId, long userId, boolean isPositive) {
        delete(REMOVE_REACTION_QUERY, reviewId, userId);
        insert(ADD_REACTION_QUERY, reviewId, userId, isPositive);
        update(UPDATE_USEFUL_QUERY, reviewId, reviewId);
    }

    public void removeReaction(long reviewId, long userId) {
        delete(REMOVE_REACTION_QUERY, reviewId, userId);
        update(UPDATE_USEFUL_QUERY, reviewId, reviewId);
    }
}