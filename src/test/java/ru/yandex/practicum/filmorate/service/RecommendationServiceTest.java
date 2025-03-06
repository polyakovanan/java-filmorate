package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private LikeStorage likeStorage;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void shouldReturnEmptyRecommendationsForNewUser() {
        User user = createUser(1L);
        when(userStorage.getById(1L)).thenReturn(Optional.of(user));
        when(likeStorage.findAll()).thenReturn(List.of());

        List<Film> recommendations = recommendationService.getRecommendations(1L);

        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnRecommendationsBasedOnSimilarUser() {
        User user1 = createUser(1L);
        User user2 = createUser(2L);
        Film film1 = createFilm(1L);
        Film film2 = createFilm(2L);
        Film film3 = createFilm(3L);

        when(userStorage.getById(2L)).thenReturn(Optional.of(user2));
        when(filmStorage.getById(2L)).thenReturn(Optional.of(film2));
        when(likeStorage.findAll()).thenReturn(List.of(
                new Like(1L, 1L),
                new Like(1L, 2L),
                new Like(2L, 1L),
                new Like(2L, 3L)
        ));

        List<Film> recommendations = recommendationService.getRecommendations(2L);

        assertEquals(1, recommendations.size());
        assertEquals(film2.getId(), recommendations.get(0).getId());
    }

    @Test
    void shouldThrowExceptionForNonExistentUser() {
        when(userStorage.getById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                recommendationService.getRecommendations(999L)
        );
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .login("user" + id)
                .name("User " + id)
                .email("user" + id + "@test.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }

    private Film createFilm(Long id) {
        return Film.builder()
                .id(id)
                .name("Film " + id)
                .description("Test film")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .build();
    }
}