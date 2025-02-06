package ru.yandex.practicum.filmorate.storage.mparating;

import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;
import java.util.Optional;

public interface MPARatingStorage {
    List<MPARating> getAll();

    Optional<MPARating> getById(long id);
}
