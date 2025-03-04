package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    public List<Film> getRecommendations(Long userId) {
        if (userStorage.getById(userId).isEmpty()) {
            log.error("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }

        List<Like> allLikes = likeStorage.findAll();
        Map<Long, Integer> similarUsers = findSimilarUsers(userId, allLikes);

        Long mostSimilarUserId = similarUsers.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (mostSimilarUserId == null) {
            log.info("Не найдены похожие пользователи для id = {}", userId);
            return Collections.emptyList();
        }

        List<Film> recommendations = getRecommendedFilms(userId, mostSimilarUserId, allLikes);
        log.info("Найдено {} рекомендаций для пользователя с id = {}", recommendations.size(), userId);
        return recommendations;
    }

    private Map<Long, Integer> findSimilarUsers(Long userId, List<Like> allLikes) {
        Set<Long> userLikes = allLikes.stream()
                .filter(like -> like.getUserId().equals(userId))
                .map(Like::getFilmId)
                .collect(Collectors.toSet());

        Map<Long, Integer> similarUsers = new HashMap<>();
        allLikes.stream()
                .filter(like -> !like.getUserId().equals(userId))
                .forEach(like -> {
                    if (userLikes.contains(like.getFilmId())) {
                        similarUsers.merge(like.getUserId(), 1, Integer::sum);
                    }
                });

        return similarUsers;
    }

    private List<Film> getRecommendedFilms(Long userId, Long similarUserId, List<Like> allLikes) {
        Set<Long> userLikedFilms = allLikes.stream()
                .filter(like -> like.getUserId().equals(userId))
                .map(Like::getFilmId)
                .collect(Collectors.toSet());

        return allLikes.stream()
                .filter(like -> like.getUserId().equals(similarUserId))
                .map(Like::getFilmId)
                .filter(filmId -> !userLikedFilms.contains(filmId))
                .distinct()
                .map(filmId -> filmStorage.getById(filmId).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}