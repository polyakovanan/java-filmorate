package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(exclude = "isAccepted")
public class Friendship {
    Long userId;
    Long friendId;
    boolean isAccepted;
}
