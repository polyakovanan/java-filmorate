package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryDirectorStorage implements DirectorStorage {
    Map<Long, Director> directors = new HashMap<>();

    @Override
    public List<Director> getAll() {
        return directors.values().stream().toList();
    }

    @Override
    public Optional<Director> getById(long id) {
        return directors.get(id) == null ? Optional.empty() : Optional.of(directors.get(id));
    }

    @Override
    public Director create(Director director) {
        director.setId(getNextId());
        directors.put(director.getId(), director);
        return director;
    }

    @Override
    public Director update(Director director) {
        directors.put(director.getId(), director);
        return director;
    }

    @Override
    public void delete(long id) {
        directors.remove(id);
    }

    @Override
    public void clear() {
        directors.clear();
    }

    private long getNextId() {
        long currentMaxId = directors.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
