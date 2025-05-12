package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.List;

public interface FilmGenres {
    void setFilmGenres(Film film);

    LinkedHashSet<Genre> getFilmGenres(Film film);

    List<Film> getPopularFilms(int count);
}
