package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        log.info("Запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        log.info("Запрос на получение фильма с id = {}", id);
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public List<Film> findPopular(@RequestParam(defaultValue = "10") int count) {
        return filmService.findPopular(count);
    }

    @GetMapping("/common")
    public List<Film> findCommon(@RequestParam long userId,
                                  @RequestParam long friendId) {
        return filmService.findCommon(userId, friendId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @Valid Film film) {

        log.info("Запрос на создание фильма");
        log.debug(film.toString());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @Valid Film newFilm) {

        log.info("Запрос на обновление фильма");
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на лайк фильма");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос удаление лайка с фильма");
        filmService.removeLike(id, userId);
    }

    @DeleteMapping("/{filmId}")
    public ResponseEntity<Void> deleteFilm(@PathVariable long filmId) {
        filmService.deleteFilm(filmId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findByDirector(@PathVariable Long directorId, @RequestParam String sortBy) {
        return filmService.findByDirector(directorId, sortBy);
    }
}