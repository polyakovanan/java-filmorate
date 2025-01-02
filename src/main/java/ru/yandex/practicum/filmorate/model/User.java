package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id;

    @NotNull(message = "Не передан параметр email'а")
    @NotBlank(message = "Email должен быть не пустым")
    @Email(message = "Email имеет некорректный формат")
    String email;

    @NotNull(message = "Не передан параметр логина")
    @NotBlank(message = "Логин должен быть не пустым")
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения должна быть в прошлом")
    LocalDate birthday;
}
