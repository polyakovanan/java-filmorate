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
        if (existingReview.isPresent()) {
            review.setUseful(existingReview.get().getUseful()); // Preserve the useful count
        }
        return reviewRepository.update(review);
    }

    @Override
    public void delete(long id) {
        reviewRepository.delete(id);
    }

    @Override
    public void addLike(long id, long userId) {
        reviewRepository.addLike(id, userId);
    }

    @Override
    public void addDislike(long id, long userId) {
        reviewRepository.addDislike(id, userId);
    }

    @Override
    public void removeLike(long id, long userId) {
        reviewRepository.removeLike(id, userId);
    }

    @Override
    public void removeDislike(long id, long userId) {
        reviewRepository.removeDislike(id, userId);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Очистка таблицы БД не поддерживается");
    }
}