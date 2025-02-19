package ru.yandex.practicum.filmorate.controller.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.friendship.InMemoryFriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UserController.class, UserService.class, InMemoryUserStorage.class, InMemoryFriendshipStorage.class, ApplicationContext.class})
abstract class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private FriendshipStorage friendshipStorage;

    @BeforeEach
    void init() {
        userStorage.clear();
        friendshipStorage.clear();
    }

    @Test
    void userControllerCreatesCorrectUser() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");
        Assertions.assertEquals("Тестовый пользователь", users.iterator().next().getName(), "Контроллер создал некорректного пользователя");
    }

    @Test
    void userControllerFillsEmptyNameWithLogin() {
        User user = User.builder()
                .login("test")
                .name("")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");
        Assertions.assertEquals("test", users.iterator().next().getName(), "Контроллер не присвоил логин в пустое имя пользователя");
    }

    @Test
    void userControllerRejectsDuplicateEmail() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        User user2 = User.builder()
                .login("test2")
                .name("Тестовый пользователь 2")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        User user2 = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test2@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

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
        User user = User.builder()
                .login("test ")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ValidationException thrown = assertThrows(
                ValidationException.class,
                () -> userController.create(user),
                "Контроллер не выкинул исключение о пробеле в логине"
        );

        assertTrue(thrown.getReason().contains("Логин не может содержать пробелы"));
    }

    @Test
    void userControllerUpdatesUser() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер не создал пользователя");

        User user2 = User.builder()
                .id(user.getId())
                .login("test")
                .name("Измененное описание")
                .email("test2@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        userController.update(user2);
        users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Контроллер создал лишнего пользователя");

        Assertions.assertEquals("Измененное описание", users.iterator().next().getName(), "Контроллер не обновил пользователя");
    }

    @Test
    void userControllerRejectsUpdateWithoutId() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();

        ConditionsNotMetException thrown = assertThrows(
                ConditionsNotMetException.class,
                () -> userController.update(user),
                "Контроллер не выкинул исключение об отсутствии Id пользователя"
        );

        assertTrue(thrown.getMessage().contains("Id должен быть указан"));
    }

    @Test
    void userControllerRejectsUpdateOfAbsentId() {
        User user = User.builder()
                .id(2L)
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.update(user),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id "
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + user.getId() + " не найден"));
    }

    @Test
    void userControllerFindsUserById() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        user = User.builder()
                .login("test2")
                .name("Тестовый пользователь 2")
                .email("test2@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        userController.addFriend(1L, 2L);
        List<User> friends = userController.findFriends(1L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не добавил пользователя в друзья");
        Assertions.assertEquals("test2", friends.get(0).getLogin(), "Контроллер не добавил пользователя в друзья");
    }

    @Test
    void userControllerRefusesAddFriendOfUnknownUser() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        user = User.builder()
                .login("test2")
                .name("Тестовый пользователь 2")
                .email("test2@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        userController.addFriend(1L, 2L);
        List<User> friends = userController.findFriends(1L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не добавил пользователя в друзья");
        Assertions.assertEquals("test2", friends.get(0).getLogin(), "Контроллер не добавил пользователя в друзья");

        userController.removeFriend(1L, 2L);
        friends = userController.findFriends(1L);
        Assertions.assertEquals(0, friends.size(), "Контроллер не удалил пользователя из друзей");
    }

    @Test
    void userControllerRefusesRemoveFriendOfUnknownUser() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        user = User.builder()
                .login("test2")
                .name("Тестовый пользователь 2")
                .email("test2@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        user = User.builder()
                .login("test3")
                .name("Тестовый пользователь 3")
                .email("test3@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        userController.addFriend(1L, 3L);
        userController.addFriend(3L, 1L);
        userController.addFriend(2L, 3L);
        userController.addFriend(3L, 2L);
        List<User> friends = userController.findCommonFriends(1L, 2L);
        Assertions.assertEquals(1, friends.size(), "Контроллер не нашел общих друзей");
        Assertions.assertEquals("test3", friends.get(0).getLogin(), "Контроллер неправильно нашел общих друзей");
   }

    @Test
    void userControllerRefusesFindCommonFriendsOfUnknownUser() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
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
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> userController.findCommonFriends(1L, 2L),
                "Контроллер не выкинул исключение об отсутствии пользователя по Id"
        );

        assertTrue(thrown.getMessage().contains("Пользователь с id = " + 2L + " не найден"));
    }

    @Test
    void friendshipStorageFindsAllFriends() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        user = User.builder()
                .login("test2")
                .name("Тестовый пользователь 2")
                .email("test2@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        user = User.builder()
                .login("test3")
                .name("Тестовый пользователь 3")
                .email("test3@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        userController.addFriend(1L, 3L);
        userController.addFriend(3L, 1L);
        userController.addFriend(2L, 3L);
        userController.addFriend(3L, 2L);

        List<Friendship> friendships = friendshipStorage.findAll();
        assertEquals(4, friendships.size(), "Контроллер не нашел списки друзей");
    }

}
