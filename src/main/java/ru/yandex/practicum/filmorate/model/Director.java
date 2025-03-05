package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Director {
    Long id;

    @NotNull(message = "Имя режиссера не может быть пустым")
    @NotBlank(message = "Имя режиссера не должно быть не пустым")
    String name;
}
