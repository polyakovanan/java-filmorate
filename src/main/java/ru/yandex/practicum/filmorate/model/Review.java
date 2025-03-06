package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private Long reviewId;

    @NotNull(message = "Не передан текст отзыва")
    @NotBlank(message = "Текст отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Не указан тип отзыва")
    private Boolean isPositive;

    @NotNull(message = "Не указан id пользователя")
    private Long userId;

    @NotNull(message = "Не указан id фильма")
    private Long filmId;

    private Integer useful;
}