package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    List<Review> getAll(Long filmId, int count);

    Optional<Review> getById(long id);

    Review create(Review review);

    Review update(Review review);

    void delete(long id);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void removeLike(long reviewId, long userId);

    void removeDislike(long reviewId, long userId);

    void clear();
}