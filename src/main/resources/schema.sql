CREATE TABLE IF NOT EXISTS films (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(100),
  description varchar(255),
  release_date date,
  duration integer,
  mpa_rating integer
);

CREATE TABLE IF NOT EXISTS users (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  email varchar(50),
  login varchar(50),
  name varchar(100),
  birthday date
);

CREATE TABLE IF NOT EXISTS likes (
  user_id integer,
  film_id integer,
  PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS friendships (
  user_id integer,
  friend_id integer,
  is_accepted bool,
  PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS genres (
  id integer GENERATED BY DEFAULT AS IDENTITY  PRIMARY KEY,
  name varchar(50)
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id integer,
  genre_id integer,
  PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
  id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  name varchar(50)
);

ALTER TABLE likes ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE likes ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE friendships ADD FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE friendships ADD FOREIGN KEY (friend_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE film_genres ADD FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE;

ALTER TABLE film_genres ADD FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE;

ALTER TABLE films ADD FOREIGN KEY (mpa_rating) REFERENCES mpa_ratings (id) ON DELETE CASCADE;
