package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPARating;

import java.util.List;
import java.util.Optional;

@Repository
public class MPARatingRepository extends BaseRepository<MPARating> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_ratings";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";

    public MPARatingRepository(JdbcTemplate jdbc, RowMapper<MPARating> mapper) {
        super(jdbc, mapper);
    }

    public List<MPARating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<MPARating> findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

}