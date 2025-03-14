package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    static final String NOT_FOUND_MESSAGE = "Отзыв с id = %s не найден";
    final ReviewStorage reviewStorage;
    final EventStorage eventStorage;
    final UserService userService;
    final FilmService filmService;

    public List<Review> findAll(Long filmId, int count) {
        if (filmId != null) {
            filmService.findById(filmId);
        }
        return reviewStorage.getAll(filmId, count);
    }

    public Review findById(Long id) {
        Optional<Review> review = reviewStorage.getById(id);
        return review.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    public Review create(Review review) {
        validateReview(review);
        Review createdReview = reviewStorage.create(review);
        log.info("Отзыв создан");
        log.debug(createdReview.toString());
        eventStorage.create(createdReview.getUserId(), createdReview.getReviewId(), EventType.REVIEW, EventOperation.ADD);
        return createdReview;
    }

    public Review update(Review review) {
        if (review.getReviewId() == null) {
            log.error("Не указан id отзыва");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Optional<Review> reviewOptional = reviewStorage.getById(review.getReviewId());
        reviewOptional.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, review.getReviewId())));
        validateReview(review);
        Review updatedReview = reviewStorage.update(review);
        log.info("Отзыв обновлен");
        eventStorage.create(updatedReview.getUserId(), updatedReview.getReviewId(), EventType.REVIEW, EventOperation.UPDATE);
        return updatedReview;
    }

    public void delete(Long id) {
        Review review = findById(id);
        reviewStorage.delete(id);
        eventStorage.create(review.getUserId(), review.getReviewId(), EventType.REVIEW, EventOperation.REMOVE);
        log.info("Отзыв с id = {} удален", id);
    }

    public void addLike(Long id, Long userId) {
        findById(id);
        userService.findById(userId);
        reviewStorage.addLike(id, userId);
        log.info("Пользователь с id = {} поставил лайк отзыву с id = {}", userId, id);
    }

    public void addDislike(Long id, Long userId) {
        findById(id);
        userService.findById(userId);
        reviewStorage.addDislike(id, userId);
        log.info("Пользователь с id = {} поставил дизлайк отзыву с id = {}", userId, id);
    }

    public void removeLike(Long id, Long userId) {
        findById(id);
        userService.findById(userId);
        reviewStorage.removeLike(id, userId);
        log.info("Пользователь с id = {} удалил лайк с отзыва с id = {}", userId, id);
    }

    public void removeDislike(Long id, Long userId) {
        findById(id);
        userService.findById(userId);
        reviewStorage.removeDislike(id, userId);
        log.info("Пользователь с id = {} удалил дизлайк с отзыва с id = {}", userId, id);
    }

    private void validateReview(Review review) {
        userService.findById(review.getUserId());
        filmService.findById(review.getFilmId());
    }
}