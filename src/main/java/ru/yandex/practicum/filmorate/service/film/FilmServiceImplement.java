package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FilmServiceImplement implements FilmService{
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
}
