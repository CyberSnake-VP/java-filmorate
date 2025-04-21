package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmService {
    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> getAll();

    Film getById(Long filmId);

    Film addLike(Long id, Long userId);

    Film deleteLike(Long id, Long userId);

    Collection<Film> getPopular (Long count);
}
