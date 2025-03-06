package ru.yandex.practicum.filmorate.controller.review;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MPARatingStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class ReviewControllerTest {

    @Autowired
    protected ReviewController reviewController;

    @Autowired
    protected FilmController filmController;

    @Autowired
    protected UserController userController;

    @Autowired
    protected ReviewStorage reviewStorage;

    @Autowired
    protected FilmStorage filmStorage;

    @Autowired
    protected UserStorage userStorage;

    @Autowired
    protected MPARatingStorage mpaRatingStorage;

    @Autowired
    protected GenreStorage genreStorage;

    @BeforeEach
    void init() {
        reviewStorage.clear();
        filmStorage.clear();
        userStorage.clear();
    }

    @Test
    void reviewControllerCreatesCorrectReview() {
        User user = createTestUser();
        Film film = createTestFilm();
        Review review = createTestReview(user.getId(), film.getId());

        reviewController.create(review);
        List<Review> reviews = reviewController.findAll(null, 10);

        assertEquals(1, reviews.size(), "Контроллер не создал отзыв");
        assertEquals(review.getContent(), reviews.getFirst().getContent(), "Контроллер создал некорректный отзыв");
        assertEquals(0, reviews.getFirst().getUseful(), "Начальный рейтинг отзыва должен быть 0");
    }

    @Test
    void reviewControllerUpdatesReview() {
        User user = createTestUser();
        Film film = createTestFilm();
        Review review = createTestReview(user.getId(), film.getId());

        Review createdReview = reviewController.create(review);
        createdReview.setContent("Updated content");

        Review updatedReview = reviewController.update(createdReview);
        assertEquals("Updated content", updatedReview.getContent(), "Контроллер не обновил отзыв");
    }

    @Test
    void reviewControllerDeletesReview() {
        User user = createTestUser();
        Film film = createTestFilm();
        Review review = createTestReview(user.getId(), film.getId());

        Review createdReview = reviewController.create(review);
        reviewController.delete(createdReview.getReviewId());

        List<Review> reviews = reviewController.findAll(null, 10);
        assertEquals(0, reviews.size(), "Контроллер не удалил отзыв");
    }

    @Test
    void reviewControllerHandlesLikesAndDislikes() {
        User user = createTestUser();
        User otherUser = createTestUser();
        Film film = createTestFilm();
        Review review = createTestReview(user.getId(), film.getId());

        Review createdReview = reviewController.create(review);

        reviewController.addLike(createdReview.getReviewId(), otherUser.getId());
        Review reviewWithLike = reviewController.findById(createdReview.getReviewId());
        assertEquals(1, reviewWithLike.getUseful(), "Контроллер не учел лайк");

        reviewController.addDislike(createdReview.getReviewId(), user.getId());
        Review reviewWithDislike = reviewController.findById(createdReview.getReviewId());
        assertEquals(0, reviewWithDislike.getUseful(), "Контроллер не учел дизлайк");
    }

    @Test
    void reviewControllerRejectsInvalidReviewId() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> reviewController.findById(999L),
                "Контроллер не выкинул исключение об отсутствии отзыва"
        );

        assertTrue(thrown.getMessage().contains("Отзыв с id = 999 не найден"));
    }

    @Test
    void reviewControllerFiltersReviewsByFilmId() {
        User user = createTestUser();
        Film film1 = createTestFilm();
        Film film2 = createTestFilm();

        Review review1 = createTestReview(user.getId(), film1.getId());
        Review review2 = createTestReview(user.getId(), film2.getId());

        reviewController.create(review1);
        reviewController.create(review2);

        List<Review> filmReviews = reviewController.findAll(film1.getId(), 10);
        assertEquals(1, filmReviews.size(), "Контроллер должен вернуть только отзывы для указанного фильма");
        assertEquals(film1.getId(), filmReviews.get(0).getFilmId(), "Контроллер вернул отзыв для неправильного фильма");
    }

    @Test
    void reviewControllerLimitsNumberOfReviews() {
        User user = createTestUser();
        Film film = createTestFilm();

        for (int i = 0; i < 15; i++) {
            Review review = createTestReview(user.getId(), film.getId());
            reviewController.create(review);
        }

        List<Review> reviews = reviewController.findAll(null, 10);
        assertEquals(10, reviews.size(), "Контроллер должен ограничить количество отзывов до 10 по умолчанию");

        reviews = reviewController.findAll(null, 5);
        assertEquals(5, reviews.size(), "Контроллер должен ограничить количество отзывов до указанного значения");
    }

    @Test
    void reviewControllerRemovesLikeAndDislike() {
        User user = createTestUser();
        User otherUser = createTestUser();
        Film film = createTestFilm();
        Review review = createTestReview(user.getId(), film.getId());

        Review createdReview = reviewController.create(review);

        // Add and remove like
        reviewController.addLike(createdReview.getReviewId(), otherUser.getId());
        Review reviewWithLike = reviewController.findById(createdReview.getReviewId());
        assertEquals(1, reviewWithLike.getUseful(), "Контроллер не учел лайк");

        reviewController.removeLike(createdReview.getReviewId(), otherUser.getId());
        Review reviewAfterLikeRemoval = reviewController.findById(createdReview.getReviewId());
        assertEquals(0, reviewAfterLikeRemoval.getUseful(), "Контроллер не удалил лайк");

        // Add and remove dislike
        reviewController.addDislike(createdReview.getReviewId(), user.getId());
        Review reviewWithDislike = reviewController.findById(createdReview.getReviewId());
        assertEquals(-1, reviewWithDislike.getUseful(), "Контроллер не учел дизлайк");

        reviewController.removeDislike(createdReview.getReviewId(), user.getId());
        Review reviewAfterDislikeRemoval = reviewController.findById(createdReview.getReviewId());
        assertEquals(0, reviewAfterDislikeRemoval.getUseful(), "Контроллер не удалил дизлайк");
    }

    private User createTestUser() {
        long timestamp = System.nanoTime();
        User user = User.builder()
                .login("test" + timestamp)
                .name("Test User")
                .email("test" + timestamp + "@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        return userController.create(user);
    }

    private Film createTestFilm() {
        long timestamp = System.nanoTime();
        Film film = Film.builder()
                .name("Test Film " + timestamp)
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1L).orElseThrow())
                .genres(List.of(genreStorage.getById(1L).orElseThrow()))
                .build();
        return filmController.create(film);
    }

    private Review createTestReview(Long userId, Long filmId) {
        return Review.builder()
                .content("Test Review Content")
                .isPositive(true)
                .userId(userId)
                .filmId(filmId)
                .build();
    }
}