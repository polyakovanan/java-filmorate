package ru.yandex.practicum.filmorate.storage.mparating;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPARating;
import ru.yandex.practicum.filmorate.storage.dal.repository.MPARatingRepository;

import java.util.List;
import java.util.Optional;

@Component("dbMPARatingStorage")
@Primary
@RequiredArgsConstructor
public class DbMPARatingStorage implements MPARatingStorage{
    final MPARatingRepository mpaRatingRepository;

    @Override
    public List<MPARating> getAll() {
        return mpaRatingRepository.findAll();
    }

    @Override
    public Optional<MPARating> getById(long id) {
        return mpaRatingRepository.findById(id);
    }
}
