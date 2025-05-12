package ru.yandex.practicum.filmorate.service.indb;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Qualifier("FilmDbService")
public class FilmDbService implements FilmService {
    private FilmStorage filmStorage;

    public FilmDbService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(Film newFilm) {
        return null;
    }

    @Override
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film getById(Long filmId) {
        return null;
    }

    @Override
    public Film addLike(Long id, Long userId) {
        return null;
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        return null;
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        return List.of();
    }
}
