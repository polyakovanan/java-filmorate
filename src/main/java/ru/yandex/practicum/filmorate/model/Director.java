package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    Long id;

    @NotBlank(message = "Имя режиссера не должно быть не пустым")
    String name;
}
