package ru.yandex.practicum.filmorate.service.indb;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmGenresService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Set;

@Service
@Qualifier("FilmDbService")
public class FilmDbService implements FilmService, FilmGenresService {
    private final FilmStorage filmStorage;

    public FilmDbService(@Qualifier("FilmDbStorage") FilmStorage filmStorage) {
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
    public Film addLike(Long film_id, Long userId) {
        return filmStorage.addLike(film_id, userId);
    }

    @Override
    public Film deleteLike(Long film_id, Long userId) {
        return filmStorage.deleteLike(film_id, userId);
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        return filmStorage.getPopular(count);
    }

    @Override
    public Set<Genre> getFilmGenres(Film film) {
        return Set.of();
    }

}
