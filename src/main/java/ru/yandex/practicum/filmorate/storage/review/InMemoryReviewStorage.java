package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryReviewStorage implements ReviewStorage {
    private final Map<Long, Review> reviews = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();
    private final Map<Long, Set<Long>> dislikes = new HashMap<>();

    @Override
    public List<Review> getAll(Long filmId, int count) {
        return reviews.values().stream()
                .filter(review -> filmId == null || review.getFilmId().equals(filmId))
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .limit(count)
                .toList();
    }

    @Override
    public Optional<Review> getById(long id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public Review create(Review review) {
        review.setReviewId(getNextId());
        review.setUseful(0);
        reviews.put(review.getReviewId(), review);
        likes.put(review.getReviewId(), new HashSet<>());
        dislikes.put(review.getReviewId(), new HashSet<>());
        return review;
    }

    @Override
    public Review update(Review review) {
        reviews.put(review.getReviewId(), review);
        return review;
    }

    @Override
    public void delete(long id) {
        reviews.remove(id);
        likes.remove(id);
        dislikes.remove(id);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        likes.get(reviewId).add(userId);
        dislikes.get(reviewId).remove(userId);
        updateUseful(reviewId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        dislikes.get(reviewId).add(userId);
        likes.get(reviewId).remove(userId);
        updateUseful(reviewId);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        likes.get(reviewId).remove(userId);
        updateUseful(reviewId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        dislikes.get(reviewId).remove(userId);
        updateUseful(reviewId);
    }

    @Override
    public void clear() {
        reviews.clear();
        likes.clear();
        dislikes.clear();
    }

    private long getNextId() {
        long currentMaxId = reviews.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void updateUseful(long reviewId) {
        Review review = reviews.get(reviewId);
        review.setUseful(likes.get(reviewId).size() - dislikes.get(reviewId).size());
    }
}