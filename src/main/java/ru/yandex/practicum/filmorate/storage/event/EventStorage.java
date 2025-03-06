package ru.yandex.practicum.filmorate.storage.event;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventOperation;
import ru.yandex.practicum.filmorate.model.event.EventType;

import java.util.List;

public interface EventStorage {

    List<Event> findByUserId(Long id);

    void create(@NotNull(message = "Не указан id пользователя") Long userId, Long entityId, EventType eventType, EventOperation eventOperation);

    void clear();
}
