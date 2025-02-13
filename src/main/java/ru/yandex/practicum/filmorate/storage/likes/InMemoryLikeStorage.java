package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component("inMemoryLikeStorage")
public class InMemoryLikeStorage implements LikeStorage {
    final Set<Like> likes = new HashSet<>();

    @Override
    public Set<Long> getFilmsByUserId(long id) {
        return likes.stream()
                .filter(like -> like.getUserId() == id)
                .map(Like::getFilmId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getUsersByFilmId(long id) {
        return likes.stream()
                .filter(like -> like.getFilmId() == id)
                .map(Like::getUserId)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> getPopularFilms(int count) {
        return likes.stream()
                .collect(Collectors.groupingBy(Like::getFilmId, Collectors.counting())).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public void create(long userId, long filmId) {
        Optional<Like> likeOp = likes.stream()
                .filter(f -> f.getUserId() == userId && f.getFilmId() == filmId)
                .findFirst();
        if ((likeOp.isEmpty())) {
            likes.add(new Like(userId, filmId));
        }
    }

    @Override
    public void remove(long userId, long filmId) {
        Optional<Like> likeOp = likes.stream()
                .filter(f -> f.getUserId() == userId && f.getFilmId() == filmId)
                .findFirst();
        likeOp.ifPresent(likes::remove);
    }

    @Override
    public void clear() {
        likes.clear();
    }
}
