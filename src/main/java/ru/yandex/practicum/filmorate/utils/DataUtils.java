package ru.yandex.practicum.filmorate.utils;

import java.time.format.DateTimeFormatter;

public class DataUtils {
    private DataUtils() {

    }

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}
