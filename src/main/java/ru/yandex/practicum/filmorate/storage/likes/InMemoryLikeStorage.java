package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.*;

@Component
public class InMemoryLikeStorage implements LikeStorage {
    final Set<Like> likes = new HashSet<>();

    @Override
    public List<Like> findAll() {
        return new ArrayList<>(likes);
    }

    @Override
    public List<Like> findByFilmIds(List<Long> filmIds) {
        return likes.stream()
                .filter(like -> filmIds.contains(like.getFilmId()))
                .toList();
    }

    @Override
    public List<Like> findCommon(long userId, long friendId) {
        return likes.stream().filter(like -> like.getUserId() == userId || like.getUserId() == friendId).toList();
    }

    public Long findCountByFilmId(long filmId) {
        return likes.stream()
                .filter(f -> f.getFilmId() == filmId)
                .count();
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
