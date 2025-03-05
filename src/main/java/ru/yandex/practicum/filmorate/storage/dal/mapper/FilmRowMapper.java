package ru.yandex.practicum.filmorate.storage.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.IntStream;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        List<String> genreIds = resultSet.getString("genre_ids") != null
                                ? List.of(resultSet.getString("genre_ids").split(", "))
                                : new ArrayList<>();

        List<String> genreNames = resultSet.getString("genre_names") != null
                                ? List.of(resultSet.getString("genre_names").split(", "))
                                : new ArrayList<>();

        Set<Genre> genres = new HashSet<>();
        IntStream.range(0, genreIds.size()).forEach(i -> genres.add(Genre.builder().id(Long.parseLong(genreIds.get(i))).name(genreNames.get(i)).build()));

        List<String> directorIds = resultSet.getString("director_ids") != null
                ? List.of(resultSet.getString("director_ids").split(", "))
                : new ArrayList<>();

        List<String> directorNames = resultSet.getString("director_names") != null
                ? List.of(resultSet.getString("director_names").split(", "))
                : new ArrayList<>();
        Set<Director> directors = new HashSet<>();
        IntStream.range(0, directorIds.size()).forEach(i -> directors.add(Director.builder().id(Long.parseLong(directorIds.get(i))).name(directorNames.get(i)).build()));

        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpa(MPARating.builder().id(resultSet.getLong("mpa_rating")).name(resultSet.getString("mpa_name")).build())
                .genres(new ArrayList<>(genres))
                .directors(new ArrayList<>(directors))
                .build();
    }
}
