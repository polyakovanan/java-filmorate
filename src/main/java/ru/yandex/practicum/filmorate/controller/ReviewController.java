package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping
    public List<Review> findAll(@RequestParam(required = false) Long filmId,
                                @RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрос на получение отзывов");
        return reviewService.findAll(filmId, count);
    }

    @GetMapping("/{id}")
    public Review findById(@PathVariable Long id) {
        log.info("Запрос на получение отзыва с id = {}", id);
        return reviewService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review create(@RequestBody @Valid Review review) {
        log.info("Запрос на создание отзыва");
        log.debug(review.toString());
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody @Valid Review review) {
        log.info("Запрос на обновление отзыва");
        log.debug(review.toString());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Запрос на удаление отзыва с id = {}", id);
        reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на добавление лайка отзыву с id = {} от пользователя с id = {}", id, userId);
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на добавление дизлайка отзыву с id = {} от пользователя с id = {}", id, userId);
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на удаление лайка отзыву с id = {} от пользователя с id = {}", id, userId);
        reviewService.removeLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на удаление дизлайка отзыву с id = {} от пользователя с id = {}", id, userId);
        reviewService.removeDislike(id, userId);
    }
}