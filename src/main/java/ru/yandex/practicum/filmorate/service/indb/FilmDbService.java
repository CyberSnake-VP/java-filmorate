package ru.yandex.practicum.filmorate.service.indb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.indb.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.indb.MpaRatingDbStorage;
import ru.yandex.practicum.filmorate.validate.FilmValidate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Qualifier("FilmDbService")
@Slf4j
public class FilmDbService implements FilmService {
    private final FilmStorage filmStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmValidate filmValidate;
    private final MpaRatingDbStorage mpaRatingDbStorage;

    @Autowired
    public FilmDbService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, GenreDbStorage genreDbStorage, FilmValidate filmValidate, MpaRatingDbStorage mpaRatingDbStorage) {
        this.filmStorage = filmStorage;
        this.genreDbStorage = genreDbStorage;
        this.filmValidate = filmValidate;
        this.mpaRatingDbStorage = mpaRatingDbStorage;
    }

    @Override
    public Film create(Film film) {
        MpaRating mpa = mpaRatingDbStorage.getMpaRating(film.getMpa().getId());
        film.setMpa(mpa);

        filmValidate.validRelease(film);
        log.info("Film {} успешно прошел валидацию на дату.", film.getName());

        // Записываем фильм в базу
        filmStorage.create(film);

        // Получаем список жанров у фильма
        List<Genre> genres = film.getGenres();

        // Записываем в таблицу films_genres жанры фильма если они есть.
        if (Objects.nonNull(genres)) {
            List<Genre> genreList = genreDbStorage.getAllGenres();
            List<Genre> validGenres = genres.stream().filter(genreList::contains).toList();

            if(!validGenres.isEmpty()) {
                genreDbStorage.setGenresToFilm(film.getId(), genres);
            }else {
                throw new NotFoundException("Не верно указан жанр фильма.");
            }
        }
        return getById(film.getId());
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
        Film film = filmStorage.getById(filmId);
        List<Genre> genres = genreDbStorage.getGenresFilmById(filmId);
        film.setGenres(genres);
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        return filmStorage.addLike(filmId, userId);
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        return filmStorage.getPopular(count);
    }
}
