package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    final FilmRepository filmRepository;
    private final JdbcTemplate jdbcTemplate; // Declare as final

    private static final String FIND_POPULAR_QUERY = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_rating_id, " +
            "m.mpa_name " +
            "FROM films f " +
            "LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id " +
            "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " + // Keep for filtering
            "LEFT JOIN likes l ON f.film_id = l.film_id "; // Keep for popularity

    @Override
    public List<Film> getAll() {
        return filmRepository.findAll();
    }

    @Override
    public Optional<Film> getById(long id) {
        return filmRepository.findById(id);
    }

    @Override
    public List<Film> findPopular(int count, Integer genreId, Integer year) {
        // Build WHERE clause dynamically
        StringBuilder sql = new StringBuilder(FIND_POPULAR_QUERY);
        List<Object> params = new ArrayList<>(); // Use a List to build parameters
        List<String> whereClauses = new ArrayList<>();

        if (genreId != null) {
            whereClauses.add("g.genre_id = ?");
            params.add(genreId);
        }
        if (year != null) {
            whereClauses.add("EXTRACT(YEAR FROM f.release_date) = ?");
            params.add(year);
        }
        if (!whereClauses.isEmpty()) {
            sql.append(" WHERE ");
            sql.append(String.join(" AND ", whereClauses));
        }
        sql.append(" GROUP BY f.film_id, m.mpa_name, g.genre_name "); //Important: Group by all selected non-aggregated columns
        sql.append(" ORDER BY COUNT(l.user_id) DESC ");
        sql.append(" LIMIT ?");
        params.add(count);

        System.out.println("Executing SQL: " + sql); // Debugging: Print the SQL
        System.out.println("Parameters: " + params);    // Debugging Print the parameters

        return jdbcTemplate.query(sql.toString(), this::mapRowToFilm, params.toArray());
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MPARating((long) rs.getInt("mpa_rating_id"), rs.getString("mpa_name")))
                .build();

        addGenre(film);
        return film;
    }

    private void addGenre(Film film) {
        String sqlQuery = "SELECT g.genre_id, g.genre_name FROM film_genres AS fg " +
                "JOIN genres AS g ON g.genre_id = fg.genre_id WHERE film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new Genre((long) rs.getInt("genre_id"), rs.getString("genre_name")), film.getId());
        film.setGenres(genres);
    }

    @Override
    public List<Film> getCommon(long userId, long friendId) {
        return filmRepository.findCommon(userId, friendId);
    }

    @Override
    public List<Film> getByDirector(Long directorId, SortBy sortBy) {
        return filmRepository.getByDirector(directorId, sortBy);
    }

    @Override
    public Film create(Film film) {
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmRepository.update(film);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Очистка таблицы БД не поддерживается");
    }

    @Override
    public void delete(long filmId) {
        //Using Spring Data JPA
        filmRepository.deleteById(filmId);
    }
}