package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.service.RecommendationService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    public List<User> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с id = {}", id);
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody @Valid User user) {
        log.info("Запрос на создание пользователя");
        log.debug(user.toString());
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User newUser) {
        log.info("Запрос на обновление пользователя");
        log.debug(newUser.toString());
        return userService.update(newUser);
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable Long id) {
        log.info("Запрос на получение друзей пользователя с id = {}", id);
        return userService.findFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрос на получение списка общих друзей между пользователями с id = {} и id = {}", id, otherId);
        return userService.findCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на добавление друга с id {} пользователю с id = {}", friendId, id);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на удаление друга с id {} у пользователя с id = {}", friendId, id);
        userService.removeFriend(id, friendId);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @GetMapping("/{id}/feed")
    public List<Event> findFeed(@PathVariable Long id) {
        log.info("Запрос на получение событий пользователя с id = {}", id);
        return userService.findFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable Long id) {
        log.info("Запрос на получение рекомендаций для пользователя с id = {}", id);
        return recommendationService.getRecommendations(id);
    }
}