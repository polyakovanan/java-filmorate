package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping
    public Film create(@RequestBody
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                       @Valid  Film film) {

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
}