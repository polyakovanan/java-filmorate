package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dal.repository.DirectorRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Primary
public class DbDirectorStorage implements DirectorStorage {
    final DirectorRepository directorRepository;

    @Override
    public List<Director> getAll() {
        return directorRepository.findAll();
    }

    @Override
    public Optional<Director> getById(long id) {
        return directorRepository.findById(id);
    }

    @Override
    public Director create(Director director) {
        return directorRepository.create(director);
    }

    @Override
    public Director update(Director director) {
        return directorRepository.update(director);
    }

    @Override
    public void delete(long id) {
        directorRepository.delete(id);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Очистка таблицы БД не поддерживается");
    }
}
