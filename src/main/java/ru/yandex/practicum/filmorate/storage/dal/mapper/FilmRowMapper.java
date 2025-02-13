package ru.yandex.practicum.filmorate.storage.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow (ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .duration(resultSet.getInt("duration"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .mpaRating(resultSet.getLong("mapRating"))
                .genres(Arrays.stream(resultSet.getString("genres").split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toSet()))
                .build();
    }
}
