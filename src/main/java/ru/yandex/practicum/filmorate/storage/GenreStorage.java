package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenreById(int id);

    List<Genre> getAllGenres();

    List<Genre> getGenresFilmById(Long id);
}
