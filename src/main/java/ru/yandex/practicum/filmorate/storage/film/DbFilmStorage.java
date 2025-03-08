package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.model.SortBy;

import java.util.List;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class DbFilmStorage implements FilmStorage {
    final FilmRepository filmRepository;

    @Override
    public List<Film> getAll() {
        return filmRepository.findAll();
    }

    @Override
    public Optional<Film> getById(long id) {
        return filmRepository.findById(id);
    }

    @Override
    public List<Film> findPopular(int count, Integer genreId, Integer year) {
        return filmRepository.findPopular(count, genreId, year);
    }

    @Override
    public List<Film> getCommon(long userId, long friendId) {
        return filmRepository.findCommon(userId, friendId);
    }

    @Override
    public List<Film> getByDirector(Long directorId, SortBy sortBy) {
        return filmRepository.getByDirector(directorId, sortBy);
    }

    @Override
    public Film create(Film film) {
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmRepository.update(film);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Очистка таблицы БД не поддерживается");
    }

    @Override
    public void delete(long filmId) {
        //Using Spring Data JPA
        filmRepository.deleteById(filmId);
}
}
