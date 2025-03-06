package ru.yandex.practicum.filmorate.model.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    long eventId;
    long userId;
    long entityId;
    long timestamp;
    EventType eventType;
    EventOperation operation;

}