CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration INTEGER,
    mpa_rating BIGINT REFERENCES mpa_ratings (id)
);

CREATE TABLE IF NOT EXISTS genres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT REFERENCES films (id) ON DELETE CASCADE,
    genre_id BIGINT REFERENCES genres (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friendships (
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    is_accepted BOOLEAN,
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    film_id BIGINT NOT NULL REFERENCES films (id) ON DELETE CASCADE,
    useful INT DEFAULT 0,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_reviews_film FOREIGN KEY (film_id) REFERENCES films (id)
);

CREATE TABLE IF NOT EXISTS review_reactions (
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    PRIMARY KEY (review_id, user_id),
    CONSTRAINT fk_review_reactions_review FOREIGN KEY (review_id) REFERENCES reviews (review_id) ON DELETE CASCADE,
    CONSTRAINT fk_review_reactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS feed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    entity_id BIGINT NOT NULL,
    event_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_type VARCHAR(6) NOT NULL,
    event_operation VARCHAR(6) NOT NULL,
    CONSTRAINT fk_feed_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);