package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;

import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class InMemoryEventStorage implements EventStorage {
    private final HashMap<Long, List<Event>> userEvents = new HashMap<>();
    private long currentId = 0;

    @Override
    public List<Event> findByUserId(Long id) {
        return userEvents.get(id) == null ? new ArrayList<>() : userEvents.get(id).stream().sorted(Comparator.comparing(Event::getEventId).reversed()).toList();
    }

    @Override
    public void create(Long userId, Long entityId, EventType eventType, EventOperation eventOperation) {
        Event event = Event.builder()
                .eventId(++currentId)
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(eventOperation)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        userEvents.computeIfAbsent(userId, id -> new ArrayList<>()).add(event);
    }

    @Override
    public void clear() {
        userEvents.clear();
    }
}
