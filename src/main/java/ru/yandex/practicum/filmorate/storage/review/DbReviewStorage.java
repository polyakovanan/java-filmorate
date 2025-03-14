package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dal.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class DbReviewStorage implements ReviewStorage {
    final ReviewRepository reviewRepository;

    @Override
    public List<Review> getAll(Long filmId, int count) {
        return reviewRepository.findAll(filmId, count);
    }

    @Override
    public Optional<Review> getById(long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Review create(Review review) {
        return reviewRepository.create(review);
    }

    @Override
    public Review update(Review review) {
        Optional<Review> existingReview = reviewRepository.findById(review.getReviewId());
        existingReview.ifPresent(value -> {
                                           review.setUseful(value.getUseful());
                                           review.setUserId(value.getUserId());
                                           review.setFilmId(value.getFilmId());
                                          });
        return reviewRepository.update(review);
    }

    @Override
    public void delete(long id) {
        reviewRepository.delete(id);
    }

    @Override
    public void addLike(long id, long userId) {
        reviewRepository.addReaction(id, userId, true);
    }

    @Override
    public void addDislike(long id, long userId) {
        reviewRepository.addReaction(id, userId, false);
    }

    @Override
    public void removeLike(long id, long userId) {
        reviewRepository.removeReaction(id, userId);
    }

    @Override
    public void removeDislike(long id, long userId) {
        reviewRepository.removeReaction(id, userId);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Очистка таблицы БД не поддерживается");
    }
}