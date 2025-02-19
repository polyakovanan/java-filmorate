package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class FilmTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void filmValidatesBlankName() {
        Film film = Film.builder()
                .name("")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(new MPARating(1L, "name"))
                .genres(List.of(new Genre(1L,"name")))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на пустое название");
    }

    @Test
    void filmValidatesNullName() {
        Film film = Film.builder()
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(new MPARating(1L, "name"))
                .genres(List.of(new Genre(1L,"name")))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(2, violations.size(), "Не пройдена валидация на null название");
    }

    @Test
    void filmValidatesLongDescription() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description(new String(new char[201]).replace("\0", "a"))
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(new MPARating(1L, "name"))
                .genres(List.of(new Genre(1L,"name")))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на слишком длинное описание");
    }

    @Test
    void filmValidatesNegativeDuration() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(-90)
                .mpa(new MPARating(1L, "name"))
                .genres(List.of(new Genre(1L,"name")))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на отрицательную продолжительность");
    }

    @Test
    void filmValidatesCorrectData() {
        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(new MPARating(1L, "name"))
                .genres(List.of(new Genre(1L,"name")))
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        Assertions.assertEquals(0, violations.size(), "Не пройдена валидация на отрицательную продолжительность");
    }
}
