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
        User user = new User();
        user.setLogin("");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на пустой логин");
    }

    @Test
    void userValidatesNullLogin() {
        User user = new User();
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(2, violations.size(), "Не пройдена валидация на null логин");
    }

    @Test
    void userValidatesBlankEmail() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на пустой email");
    }

    @Test
    void userValidatesNullEmail() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(2, violations.size(), "Не пройдена валидация на null email");
    }

    @Test
    void userValidatesIncorrectEmail() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("testmail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на некорректный email");
    }

    @Test
    void userValidatesFutureBirthday() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2030, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(1, violations.size(), "Не пройдена валидация на день рождения в будущем email");
    }

    @Test
    void userValidatesCorrectData() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        Assertions.assertEquals(0, violations.size(), "Корректный пользователь не прошел валидацию");
    }
}
