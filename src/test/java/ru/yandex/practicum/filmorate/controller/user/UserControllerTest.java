package ru.yandex.practicum.filmorate.controller.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MPARatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class UserControllerTest {

    @Autowired
    private UserController userController;

    @Autowired
    private FilmController filmController;

    @Autowired
    private ReviewController reviewController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private FriendshipStorage friendshipStorage;

    @Autowired
    private EventStorage eventStorage;

    @Autowired
    private MPARatingStorage mpaRatingStorage;

    @Autowired
    private GenreStorage genreStorage;

    @Autowired
    private LikeStorage likeStorage;

    @BeforeEach
    void init() {
        userStorage.clear();
        friendshipStorage.clear();
        eventStorage.clear();
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

    @Test
    void userControllerFindsFeed() {
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

        Film film = Film.builder()
                .name("Тестовый фильм")
                .description("Тестовое описание фильма")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(90)
                .mpa(mpaRatingStorage.getById(1).get())
                .genres(List.of(genreStorage.getById(1).get()))
                .build();

        filmController.create(film);

        Review review = Review.builder()
                .content("Test Review Content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();

        userController.addFriend(1L, 2L);
        userController.removeFriend(1L, 2L);
        filmController.addLike(1L, 1L);
        filmController.removeLike(1L, 1L);
        reviewController.create(review);
        review.setContent("updated review");
        reviewController.update(review);
        reviewController.delete(1L);

        List<Event> feed = userController.findFeed(1L);
        Assertions.assertEquals(7, feed.size(), "Контроллер не нашел все события");
        Assertions.assertEquals(EventType.REVIEW, feed.get(6).getEventType(), "Контроллер не нашел событие об удалении отзыва");
        Assertions.assertEquals(EventOperation.REMOVE, feed.get(6).getOperation(), "Контроллер не нашел событие об удалении отзыва");
        Assertions.assertEquals(EventType.REVIEW, feed.get(5).getEventType(), "Контроллер не нашел событие об обновлении отзыва");
        Assertions.assertEquals(EventOperation.UPDATE, feed.get(5).getOperation(), "Контроллер не нашел событие об обновлении отзыва");
        Assertions.assertEquals(EventType.REVIEW, feed.get(4).getEventType(), "Контроллер не нашел событие о добавлении отзыва");
        Assertions.assertEquals(EventOperation.ADD, feed.get(4).getOperation(), "Контроллер не нашел событие о добавлении отзыва");
        Assertions.assertEquals(EventType.LIKE, feed.get(3).getEventType(), "Контроллер не нашел событие об удалении лайка");
        Assertions.assertEquals(EventOperation.REMOVE, feed.get(3).getOperation(), "Контроллер не нашел событие об удалении лайка");
        Assertions.assertEquals(EventType.LIKE, feed.get(2).getEventType(), "Контроллер не нашел событие о добавлении лайка");
        Assertions.assertEquals(EventOperation.ADD, feed.get(2).getOperation(), "Контроллер не нашел событие о добавлении лайка");
        Assertions.assertEquals(EventType.FRIEND, feed.get(1).getEventType(), "Контроллер не нашел событие об удалении друга");
        Assertions.assertEquals(EventOperation.REMOVE, feed.get(1).getOperation(), "Контроллер не нашел событие об удалении друга");
        Assertions.assertEquals(EventType.FRIEND, feed.get(0).getEventType(), "Контроллер не нашел событие о добавлении друга");
        Assertions.assertEquals(EventOperation.ADD, feed.get(0).getOperation(), "Контроллер не нашел событие о добавлении друга");

    }

    @Test
    void userControllerDeletesUser() {
        User user = User.builder()
                .login("test")
                .name("Тестовый пользователь")
                .email("test@mail.com")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
        userController.create(user);

        Assertions.assertEquals(1, userController.findAll().size(), "Контроллер не создал пользователя");

        userController.deleteUser(1L);
        Assertions.assertEquals(0, userController.findAll().size(), "Контроллер не удалил пользователя");
    }

    @Test
    void userControllerRefusesToDeleteNotExistingUser() {
        Assertions.assertThrows(NotFoundException.class, () -> userController.deleteUser(1L));
    }
}
