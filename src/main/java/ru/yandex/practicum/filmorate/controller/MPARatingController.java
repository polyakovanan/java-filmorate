package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.service.MPARatingService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MPARatingController {
    private final MPARatingService mpaRatingService;

    @GetMapping
    public List<MPARating> findAll() {
        log.info("Запрос на получение всех рейтингов");
        return mpaRatingService.findAll();
    }

    @GetMapping("/{id}")
    public MPARating findById(@PathVariable Long id) {
        log.info("Запрос на получение рейтинга с id = {}", id);
        return mpaRatingService.findById(id);
    }
}
