package ru.yandex.practicum.filmorate.storage.mparating;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component("inMemoryMPARatingStorage")
public class InMemoryMPARatingStorage implements MPARatingStorage {
    Map<Long, MPARating> ratings = new HashMap<>();

    public InMemoryMPARatingStorage() {
        ratings.put(1L, new MPARating(1L, "G"));
        ratings.put(2L, new MPARating(2L, "PG"));
        ratings.put(3L, new MPARating(3L, "PG-13"));
        ratings.put(4L, new MPARating(4L, "R"));
        ratings.put(5L, new MPARating(5L, "NC-17"));
    }

    @Override
    public List<MPARating> getAll() {
        return ratings.values().stream().toList();
    }

    @Override
    public Optional<MPARating> getById(long id) {
        return ratings.get(id) == null ? Optional.empty() : Optional.of(ratings.get(id));
    }


}
