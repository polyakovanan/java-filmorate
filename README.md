# Filmorate

> Это приложение - небольшая социальная сеть, 
> которая позволяет оценивать фильмы и искать самые популярые, 
> а так же добавлять других пользователей в друзья

### Схема БД

![](https://github.com/polyakovanan/java-filmorate/blob/main/dbdiagram.png?raw=true)

### Примеры запросов

<details>
    <summary><h3>Для фильмов:</h3></summary>

* `Создание` фильма:

```SQL
INSERT INTO films (name,
                   description,
                   release_date,
                   duration)
VALUES (?, ?, ?, ?);

INSERT INTO film_genres (film_id,
			 genre_id)
VALUES (?, ?);
```

* `Обновление` фильма:

```SQL
UPDATE
    films
SET name                = ?,
    description         = ?,
    release_date        = ?,
    duration
WHERE id = ?;

INSERT INTO film_genres (film_id,
			 genre_id)
VALUES (?, ?);

DELETE FROM film_genres
WHERE film_id = ?
AND genre_id = ?;
```

* `Получение` фильма по `id`:

```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mp.name mpa_rating,
       STRING_AGG(g.name, ', ') genres
FROM films f
	 LEFT JOIN mpa_ratings mp ON f.mpa_rating_id = mp.id
         LEFT JOIN film_genres fg ON f.film_id = fg.film_id
         LEFT JOIN genres g ON fg.genre_id = g.id
WHERE f.id = ?
GROUP BY f.id;
```   

* `Получение всех` фильмов:

```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mp.name mpa_rating,
       STRING_AGG(g.name, ', ') genres
FROM films f
         LEFT JOIN mpa_ratings mp ON f.mpa_rating_id = mp.id
         LEFT JOIN film_genres fg ON f.id = fg.film_id
         LEFT JOIN genres g ON fg.genre_id = g.id
GROUP BY f.id;
```

* `Получение топ-N (по количеству лайков)` фильмов:
```SQL
SELECT f.id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mp.name mpa_rating,
       STRING_AGG(g.name, ', ') genres,
       COUNT(fl.user_id) AS like_count
FROM films f
         LEFT JOIN mpa_ratings mp ON f.mpa_rating_id = mp.id
         LEFT JOIN film_genres fg ON f.film_id = fg.film_id
         LEFT JOIN genres g ON fg.genre_id = g.id
         LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.id
ORDER BY like_count DESC LIMIT ?;
```

* `Добавление` лайка фильму:
```SQL
INSERT INTO likes (user_id,
		   film_id)
VALUES (?, ?);
```


* `Удаление` лайка с фильма:
```SQL
DELETE FROM likes 
WHERE (user_id,= ?
AND film_id)= ?;
```
</details>

<details>
    <summary><h3>Для пользователей:</h3></summary>

* `Создание` пользователя:

```SQL
INSERT INTO users (email,
                   login,
                   name,
                   birthday)
VALUES (?, ?, ?, ?)
```

* `Обновление` пользователя:

```SQL
UPDATE
    users
SET email    = ?,
    login    = ?,
    name     = ?,
    birthday = ?
WHERE id = ?
```

* `Получение` пользователя `по идентификатору`:

```SQL
SELECT *
FROM users
WHERE id = ?
```   

* `Получение всех` пользователей:

```SQL
SELECT *
FROM users
``` 

* `Получение друзей` пользователя:

```SQL
SELECT *
FROM users
WHERE id IN (
	SELECT friend_id 
	FROM friendships
	WHERE is_accepted = 1
	AND user_id = ?
	)
``` 

* `Получение общих друзей` с пользователем:

```SQL
SELECT *
FROM users
WHERE id IN (
	SELECT friend_id 
	FROM friendships
	WHERE is_accepted = 1
	AND user_id = ?
	)
AND id IN (
	SELECT friend_id 
	FROM friendships
	WHERE is_accepted = 1
	AND user_id = ?
	)
``` 

* `Отправка заявки на добавление в друзья` пользователя:

```SQL
INSERT INTO friendships (user_id,
		   	friend_id,
			is_accepted)
VALUES (?, ?, 0);

INSERT INTO friendships (user_id,
		   	friend_id,
			is_accepted)
VALUES (?, ?, 0);
```

* `Принятие заявки на добавление в друзья` пользователя:

```SQL
UPDATE friendships
SET is_accepted = 1
WHERE user_id = ?
AND friend_id = ?;

UPDATE friendships
SET is_accepted = 1
WHERE user_id = ?
AND friend_id = ?;
```

* `Удаление из друзей` пользователя:

```SQL
DELETE FROM friendships
WHERE user_id = ?
AND friend_id = ?;

DELETE FROM friendships
WHERE user_id = ?
AND friend_id = ?;
```

</details>