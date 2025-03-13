package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private Long id;

    @NotNull(message = "Не передан параметр email'а")
    @NotBlank(message = "Email должен быть не пустым")
    @Email(message = "Email имеет некорректный формат")
    private String email;

    @NotNull(message = "Не передан параметр логина")
    @NotBlank(message = "Логин должен быть не пустым")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;
}
