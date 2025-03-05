package ru.yandex.practicum.filmorate.storage.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String BASE_FIND_QUERY = "SELECT f.*, " +
                                                  "mpa.name as mpa_name, " +
                                                  "string_agg(g.id, ', ') as genre_ids, " +
                                                  "string_agg(g.name, ', ') as genre_names, " +
                                                  "string_agg(d.id, ', ') as director_ids, " +
                                                  "string_agg(d.name, ', ') as director_names " +
                                                  "FROM films f " +
                                                  "LEFT JOIN film_genres fg on fg.film_id = f.id " +
                                                  "LEFT JOIN genres g on g.id = fg.genre_id " +
                                                  "LEFT JOIN mpa_ratings mpa on mpa.id = f.mpa_rating " +
                                                  "LEFT JOIN film_directors fd on fd.film_id = f.id " +
                                                  "LEFT JOIN directors d on d.id = fd.director_id ";

    private static final String FIND_BY_ID_QUERY = BASE_FIND_QUERY +
                                                   "WHERE f.id = ? " +
                                                   "GROUP BY f.id";

    private static final String FIND_ALL_QUERY = BASE_FIND_QUERY +
                                                 "GROUP BY f.id";

    private static final String FIND_POPULAR_QUERY = BASE_FIND_QUERY +
                                                    "LEFT JOIN likes l on l.film_id = f.id " +
                                                    "GROUP BY f.id " +
                                                    "ORDER BY count(l.user_id) DESC limit ?";

    private static final String FIND_COMMON_QUERY = BASE_FIND_QUERY +
                                                    "LEFT JOIN likes l on l.film_id = f.id " +
                                                    "WHERE f.id IN (" +
                                                        "SELECT l1.film_id " +
                                                        "FROM likes l1 " +
                                                        "JOIN likes l2 on l1.film_id = l2.film_id " +
                                                        "WHERE l1.user_id = ? AND l2.user_id = ?" +
                                                    ")" +
                                                    "GROUP BY f.id " +
                                                    "ORDER BY count(l.user_id) DESC";

    private static final String FIND_BY_DIRECTOR_YEAR_QUERY = BASE_FIND_QUERY +
                                                              "WHERE f.id IN (" +
                                                              "SELECT fd1.film_id " +
                                                              "FROM film_directors fd1 " +
                                                              "WHERE fd1.director_id = ?" +
                                                              ")" +
                                                              "GROUP BY f.id " +
                                                              "ORDER BY f.release_date";

    public static final String FIND_BY_DIRECTOR_LIKES_QUERY = BASE_FIND_QUERY +
                                                              "LEFT JOIN likes l on l.film_id = f.id " +
                                                              "WHERE f.id IN (" +
                                                              "SELECT fd1.film_id " +
                                                              "FROM film_directors fd1 " +
                                                              "WHERE fd1.director_id = ?" +
                                                              ")" +
                                                              "GROUP BY f.id " +
                                                              "ORDER BY count(l.user_id) DESC";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_GENRES_QUERY = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";
    private static final String INSERT_DIRECTORS_QUERY = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
    private static final String DELETE_DIRECTORS_QUERY = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating = ? WHERE id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById(Long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public List<Film> findPopular(int count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }

    public List<Film> findCommon(Long userId, Long friendId) {
        return findMany(FIND_COMMON_QUERY, userId, friendId);
    }

    public Film create(Film film) {
        Long id = insertWithGeneratedId(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(film.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(id);
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> insert(INSERT_GENRES_QUERY, id, genre.getId()));
        }
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> insert(INSERT_DIRECTORS_QUERY, id, director.getId()));
        }
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(film.getReleaseDate().atStartOfDay().toInstant(ZoneOffset.UTC)),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        delete(DELETE_GENRES_QUERY, film.getId());
        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> insert(INSERT_GENRES_QUERY, film.getId(), genre.getId()));
        }

        delete(DELETE_DIRECTORS_QUERY, film.getId());
        if (film.getDirectors() != null) {
            film.getDirectors().forEach(director -> insert(INSERT_DIRECTORS_QUERY, film.getId(), director.getId()));
        }
        return film;
    }

    public List<Film> getByDirector(Long directorId, SortBy sortBy) {
        return switch (sortBy) {
            case YEAR -> findMany(FIND_BY_DIRECTOR_YEAR_QUERY, directorId);
            case LIKES -> findMany(FIND_BY_DIRECTOR_LIKES_QUERY, directorId);
        };
    }
}
