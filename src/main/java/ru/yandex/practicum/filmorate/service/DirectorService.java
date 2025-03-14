package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    static final String NOT_FOUND_MESSAGE = "Режиссер с id = %s не найден";
    final DirectorStorage directorStorage;

    public List<Director> findAll() {
        return directorStorage.getAll();
    }

    public Director findById(Long id) {
        Optional<Director> director = directorStorage.getById(id);
        return director.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
    }

    public Director create(Director director) {
        Director createdDirector = directorStorage.create(director);
        log.info("Режиссер создан");
        log.debug(createdDirector.toString());
        return createdDirector;
    }

    public Director update(Director director) {
        if (director.getId() == null) {
            log.error("Не указан id режиссера");
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        Optional<Director> directorOptional = directorStorage.getById(director.getId());
        directorOptional.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, director.getId())));

        Director currentDirector = directorStorage.update(director);
        log.info("Режиссер обновлен");
        log.debug(currentDirector.toString());
        return currentDirector;
    }

    public void delete(Long id) {
        Optional<Director> director = directorStorage.getById(id);
        director.orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, id)));
        directorStorage.delete(id);
    }

}
