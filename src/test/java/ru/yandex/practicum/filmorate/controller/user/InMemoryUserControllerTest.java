package ru.yandex.practicum.filmorate.controller.user;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.director.InMemoryDirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.InMemoryEventStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.genre.InMemoryGenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.InMemoryLikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.InMemoryMPARatingStorage;
import ru.yandex.practicum.filmorate.storage.review.InMemoryReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@SpringBootTest(classes = {
        FilmController.class,
        UserController.class,
        FilmController.class,
        InMemoryUserStorage.class,
        UserService.class,
        RecommendationService.class,
        ReviewController.class,
        FilmService.class,
        UserService.class,
        ReviewService.class,
        InMemoryFilmStorage.class,
        InMemoryUserStorage.class,
        InMemoryFriendshipStorage.class,
        InMemoryGenreStorage.class,
        InMemoryMPARatingStorage.class,
        InMemoryLikeStorage.class,
        InMemoryEventStorage.class,
        InMemoryReviewStorage.class,
        InMemoryDirectorStorage.class,
        ApplicationContext.class
})
public class InMemoryUserControllerTest extends UserControllerTest {
}