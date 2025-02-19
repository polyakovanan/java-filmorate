package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

public class UserTest {
    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void userValidatesBlankLogin() {
        User user = User.builder()
                .login("")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на пустой логин");
    }

    @Test
    void userValidatesNullLogin() {
        User user = User.builder()
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(2, violations.size(), "Не пройдена валидация на null логин");
    }

    @Test
    void userValidatesBlankEmail() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на пустой email");
    }

    @Test
    void userValidatesNullEmail() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(2, violations.size(), "Не пройдена валидация на null email");
    }

    @Test
    void userValidatesIncorrectEmail() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("testmail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на некорректный email");
    }

    @Test
    void userValidatesFutureBirthday() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2060, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на день рождения в будущем email");
    }

    @Test
    void userValidatesCorrectData() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(0, violations.size(), "Корректный пользователь не прошел валидацию");
    }
}
