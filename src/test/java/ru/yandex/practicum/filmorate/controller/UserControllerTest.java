package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {UserController.class, UserService.class, InMemoryUserStorage.class, ApplicationContext.class})
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userStorage;

    @BeforeEach
    void init() {
        userStorage.clear();
    }

    @Test
    void userControllerCreatesCorrectUser() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");
        Assertions.assertEquals("Тестовый пользователь", users.iterator().next().getName(), "Контроллер создал некорректного пользователя");
    }

    @Test
    void userControllerFillsEmptyNameWithLogin() {
        User user = new User();
        user.setLogin("test");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");
        Assertions.assertEquals("test", users.iterator().next().getName(), "Контроллер не присвоил логин в пустое имя пользователя");
    }

    @Test
    void userControllerRejectsDuplicateEmail() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setLogin("test2");
        user2.setName("Тестовый пользователь");
        user2.setEmail("test@mail.com");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");

        DuplicatedDataException thrown = assertThrows(
                DuplicatedDataException.class,
                () -> userController.create(user2),
                "Контроллер не выкинул исключение о дубликате email"
        );

        assertTrue(thrown.getMessage().contains("Этот email уже используется"));
    }

    @Test
    void userControllerRejectsDuplicateLogin() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User user2 = new User();
        user2.setLogin("test");
        user2.setName("Тестовый пользователь");
        user2.setEmail("test2@mail.com");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");

        DuplicatedDataException thrown = assertThrows(
                DuplicatedDataException.class,
                () -> userController.create(user2),
                "Контроллер не выкинул исключение о дубликате логина"
        );

        assertTrue(thrown.getMessage().contains("Этот логин уже используется"));
    }

    @Test
    void userControllerRejectsWhitespaceLogin() {
        User user = new User();
        user.setLogin("test ");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> userController.create(user),
                "Контроллер не выкинул исключение о пробеле в логине"
        );

        assertTrue(thrown.getReason().contains("Логин не может содержать пробелы"));
    }

    @Test
    void userControllerUpdatesUser() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");

        User user2 = new User();
        user2.setId(user.getId());
        user2.setLogin("test");
        user2.setName("Измененное описание");
        user2.setEmail("test2@mail.com");
        user2.setBirthday(LocalDate.of(2000, 1, 1));

        userController.update(user2);
        users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер создал лишнего пользователя");

        Assertions.assertEquals("Измененное описание", users.iterator().next().getName(), "Контроллер не обновил пользователя");
    }

    @Test
    void userControllerRejectsUpdateWithoutId() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        ConditionsNotMetException thrown = assertThrows(
                ConditionsNotMetException.class,
                () -> userController.update(user),
                "Контроллер не выкинул исключение об отсутствии Id пользователя"
        );

        assertTrue(thrown.getMessage().contains("Id должен быть указан"));
    }

    @Test
    void userControllerRejectsUpdateOfAbsentId() {
        User user = new User();
        user.setId(2L);
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.update(user),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id "
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + user.getId() + " не найден"));
    }

    @Test
    void userControllerFindsUserById() {
        User user = new User();
        user.setLogin("test");
        user.setName("Тестовый пользователь");
        user.setEmail("test@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        User foundUser = userController.findById(1L);
        Assertions.assertEquals(user, foundUser, "Контроллер нашел не тот фильм по Id");
    }

    @Test
    void userControllerDoNotFindUserById() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.findById(1L),
                "Контроллер не выкинул исключение об отсутствии фильма по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 1L + " не найден"));
    }

    @Test
    void userControllerAddsFriend() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        user = new User();
        user.setLogin("test2");
        user.setName("Тестовый пользователь 2");
        user.setEmail("test2@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        userController.addFriend(1L, 2L);
        List<User> friends = userController.findFriends(1L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не добавил пользователя в друзья");
        Assertions.assertEquals("test2", friends.get(0).getLogin(), "Контроллер не добавил пользователя в друзья");

        friends = userController.findFriends(2L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не добавил пользователя в друзья другого пользователя");
        Assertions.assertEquals("test1", friends.get(0).getLogin(), "Контроллер не добавил пользователя в друзья другого пользователя");
    }

    @Test
    void userControllerRefusesAddFriendOfUnknownUser() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.addFriend(2L, 1L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 2L + " не найден"));
    }

    @Test
    void userControllerRefusesAddUnknownFriend() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.addFriend(1L, 2L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 2L + " не найден"));
    }

    @Test
    void userControllerRemovesFriends() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        user = new User();
        user.setLogin("test2");
        user.setName("Тестовый пользователь 2");
        user.setEmail("test2@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        userController.addFriend(1L, 2L);
        List<User> friends = userController.findFriends(1L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не добавил пользователя в друзья");
        Assertions.assertEquals("test2", friends.get(0).getLogin(), "Контроллер не добавил пользователя в друзья");

        friends = userController.findFriends(2L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не добавил пользователя в друзья другого пользователя");
        Assertions.assertEquals("test1", friends.get(0).getLogin(), "Контроллер не добавил пользователя в друзья другого пользователя");

        userController.removeFriend(1L, 2L);
        friends = userController.findFriends(1L);
        Assertions.assertEquals(0, friends.size(), "Контроллер не удалил пользователя из друзей");

        friends = userController.findFriends(2L);
        Assertions.assertEquals(0, friends.size(), "Контроллер не удалил пользователя из друзей другого пользователя");
    }

    @Test
    void userControllerRefusesRemoveFriendOfUnknownUser() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.removeFriend(2L, 1L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 2L + " не найден"));
    }

    @Test
    void userControllerRefusesRemoveUnknownFriend() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.removeFriend(1L, 2L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 2L + " не найден"));
    }

    @Test
    void userControllerRefusesFindFriendsOfUnknownUser() {
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.findFriends(1L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 1L + " не найден"));

    }

    @Test
    void userControllerFindsCommonFriends() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        user = new User();
        user.setLogin("test2");
        user.setName("Тестовый пользователь 2");
        user.setEmail("test2@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        user = new User();
        user.setLogin("test3");
        user.setName("Тестовый пользователь 3");
        user.setEmail("test3@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        userController.addFriend(1L, 3L);
        userController.addFriend(2L, 3L);
        List<User> friends = userController.findCommonFriends(1L, 2L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не нашел общих друзей");
        Assertions.assertEquals("test3", friends.get(0).getLogin(), "Контроллер неправильно нашел общих друзей");
   }

    @Test
    void userControllerRefusesFindCommonFriendsOfUnknownUser() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.findCommonFriends(2L, 1L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 2L + " не найден"));
    }

    @Test
    void userControllerRefusesFindCommonFriendsForUnknownFriend() {
        User user = new User();
        user.setLogin("test1");
        user.setName("Тестовый пользователь 1");
        user.setEmail("test1@mail.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.findCommonFriends(1L, 2L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 2L + " не найден"));
    }

}
