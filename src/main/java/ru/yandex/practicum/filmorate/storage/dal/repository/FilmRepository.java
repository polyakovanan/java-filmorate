package ru.yandex.practicum.filmorate.storage.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.SearchBy;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String FIND_DUPLICATE_QUERY = BASE_FIND_QUERY +
                                                  "WHERE f.name = ? " +
                                                  "AND f.release_date = ? " +
                                                  "AND f.mpa_rating = ? ";

    private static final String FIND_ALL_QUERY = BASE_FIND_QUERY +
                                                 "GROUP BY f.id";

    private static final String FIND_POPULAR_QUERY = BASE_FIND_QUERY +
                                                "LEFT JOIN likes l on l.film_id = f.id " +
                                                "WHERE 1=1 " +
                                                "AND CASE WHEN ? IS NOT NULL THEN EXTRACT(YEAR FROM f.release_date) = ? ELSE TRUE END " +
                                                "AND CASE WHEN ? IS NOT NULL THEN f.id in (SELECT fg2.film_id FROM film_genres fg2 WHERE fg2.genre_id = ?) ELSE TRUE END " +
                                                "GROUP BY f.id " +
                                                "ORDER BY count(l.user_id) DESC ";

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
    public static final String SEARCH_BY_BASE_QUERY = BASE_FIND_QUERY +
                                                      "LEFT JOIN likes l on l.film_id = f.id " +
                                                      "WHERE 1 = 0 ";
    public static final String SEARCH_BY_TITLE_QUERY = "OR LOWER(f.name) LIKE LOWER(?) ";
    public static final String SEARCH_BY_DIRECTOR_QUERY = "OR f.id IN (" +
                                                          "SELECT fd1.film_id " +
                                                          "FROM film_directors fd1 " +
                                                          "JOIN directors d1 ON fd1.director_id = d1.id AND LOWER(d1.name) LIKE LOWER(?)) ";

    private static final String INSERT_QUERY = "INSERT INTO films (name, description, release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?"; // Define it HERE
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

    public Optional<Film> findDuplicate(Film film) {
        StringBuilder sqlQuery = new StringBuilder(FIND_DUPLICATE_QUERY);
        List<Object> params = new ArrayList<>();
        params.add(film.getName());
        params.add(film.getReleaseDate());
        params.add(film.getMpa().getId());
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                sqlQuery.append("AND EXISTS (SELECT 1 FROM film_genres fg1 WHERE fg1.genre_id = ? AND fg1.film_id = f.id) ");
                params.add(genre.getId());
            }
        }
        sqlQuery.append("GROUP BY f.id");
        return findOne(sqlQuery.toString(), params.toArray());
    }

    public List<Film> findPopular(Integer count, Integer year, Long genreId) {
        if (count == null) {
            return findMany(FIND_POPULAR_QUERY,
                    year, year,
                    genreId, genreId);
        }
        return findMany(FIND_POPULAR_QUERY + " LIMIT ?",
                year, year,
                genreId, genreId,
                count);
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

    public void deleteById(long filmId) {
        jdbc.update(DELETE_QUERY, filmId);
    }

    public List<Film> search(String query, SearchBy[] searchBy) {
       String sqlQuery = SEARCH_BY_BASE_QUERY;
       List<Object> params = new ArrayList<>();
       if (Arrays.asList(searchBy).contains(SearchBy.TITLE)) {
           sqlQuery += SEARCH_BY_TITLE_QUERY;
           params.add("%" + query + "%");
       }
       if (Arrays.asList(searchBy).contains(SearchBy.DIRECTOR)) {
           sqlQuery += SEARCH_BY_DIRECTOR_QUERY;
           params.add("%" + query + "%");
       }
       sqlQuery += "GROUP BY f.id ORDER BY count(l.user_id) DESC ";
       return findMany(sqlQuery, params.toArray());
    }
}
