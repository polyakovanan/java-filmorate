package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    public static final LocalDate CINEMA_BIRTH_DAY = LocalDate.of(1895, 12, 28);

    Long id;
    @NotNull(message = "Не передан параметр названия")
    @NotBlank(message = "Название не может быть пустым")
    String name;

    @Size(max = 200, message = "Длина названия не должна превышать 200 символов")
    String description;
    LocalDate releaseDate;

    @Positive(message = "Длительность не может быть отрицательной")
    Integer duration;
    Long MPARating;

    final Set<Long> genres = new HashSet<>();
    final Set<Long> likes = new HashSet<>();

    public void addLike(Long userId) {
        likes.add(userId);
    }

    public void removeLike(Long userId) {
        likes.remove(userId);
    }

    public int getLikeCount() {
        return likes.size();
    }
}
