package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.storage.dal.repository.EventRepository;

import java.util.List;

@Component
@Primary
@RequiredArgsConstructor
public class DbEventStorage implements EventStorage {
    final EventRepository eventRepository;

    @Override
    public List<Event> findByUserId(Long id) {
        return eventRepository.findByUserId(id);
    }

    @Override
    public void create(Long userId, Long entityId, EventType eventType, EventOperation eventOperation) {
        eventRepository.create(userId, entityId, eventType, eventOperation);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Очистка таблицы БД не поддерживается");
    }
}
