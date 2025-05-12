package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;


import java.util.Set;

public interface FilmGenresService {

    Set<Genre> getFilmGenres(Film film);

}
