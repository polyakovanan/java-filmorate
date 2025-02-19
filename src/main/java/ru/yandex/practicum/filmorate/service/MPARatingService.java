package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.mparating.MPARatingStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MPARatingService {
    static final String NOT_FOUND_MESSAGE = "Рейтинг с id = %s не найден";
    final MPARatingStorage ratingStorage;

    public List<MPARating> findAll() {
        return ratingStorage.getAll();
    }

    public MPARating findById(Long id) {
        Optional<MPARating> mpaRating = ratingStorage.getById(id);
        if (mpaRating.isPresent()) {
            return mpaRating.get();
        }
        log.error(String.format(NOT_FOUND_MESSAGE, id));
        throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, id));
    }
}
