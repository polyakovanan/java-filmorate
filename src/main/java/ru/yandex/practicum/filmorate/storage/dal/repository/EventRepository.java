package ru.yandex.practicum.filmorate.storage.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;

import java.util.List;

@Repository
public class EventRepository extends BaseRepository<Event>{
    private static final String FIND_BY_USER_ID_QUERY = "SELECT * FROM feed WHERE user_id = ? ORDER BY event_time DESC";
    private static final String INSERT_QUERY = "INSERT INTO feed (user_id, entity_id, event_type, event_operation) VALUES (?, ?, ?, ?)";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public List<Event> findByUserId(long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

    public void create(Long userId, Long entityId, EventType eventType, EventOperation eventOperation) {
        insert(INSERT_QUERY, userId, entityId, eventType.toString(), eventOperation.toString());
    }
}
