package ru.yandex.practicum.filmorate.model.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private long eventId;
    private long userId;
    private long entityId;
    private long timestamp;
    private EventType eventType;
    private EventOperation operation;

}