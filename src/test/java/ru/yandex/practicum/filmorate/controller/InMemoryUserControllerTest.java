package ru.yandex.practicum.filmorate.controller;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

@SpringBootTest(classes = {UserController.class, UserService.class, InMemoryUserStorage.class, InMemoryFriendshipStorage.class, ApplicationContext.class})
public class InMemoryUserControllerTest extends UserControllerTest{
}
