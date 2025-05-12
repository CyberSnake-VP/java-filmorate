package ru.yandex.practicum.filmorate.service.inmemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
@Qualifier("FilmServiceImplement")
public class FilmServiceImplement implements FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmServiceImplement(@Qualifier("InMemoryFilmStorage") FilmStorage filmStorage, @Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film getById(Long filmId) {
        return filmStorage.getById(filmId);
    }

    @Override
    public Film addLike(Long id, Long userId) {
        return filmStorage.addLike(id, userId);
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        return filmStorage.deleteLike(id, userId);
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        return filmStorage.getPopular(count);
    }
}
