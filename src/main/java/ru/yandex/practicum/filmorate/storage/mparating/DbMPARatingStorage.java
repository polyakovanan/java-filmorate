package ru.yandex.practicum.filmorate.storage.mparating;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;
import java.util.Optional;

@Component("dbMPARatingStorage")
public class DbMPARatingStorage implements MPARatingStorage{
    @Override
    public List<MPARating> getAll() {
        return List.of();
    }

    @Override
    public Optional<MPARating> getById(long id) {
        return Optional.empty();
    }
}
