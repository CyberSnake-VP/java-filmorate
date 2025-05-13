package ru.yandex.practicum.filmorate.storage.indb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validate.FilmValidate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
@Qualifier("FilmDbStorage")
@Slf4j
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final GenreDbStorage genreDbStorage;
    private final FilmValidate filmValidate;
    private final MapRatingDbService mapRatingDbService;

    // Не проходит checkstyle в github, GET_ALL_QUERY ругался...
    private final String getAllQuery =
            "SELECT f.*, r.mpa_name " +
                    "FROM films AS f JOIN mpa_rating AS r ON f.mpa_rating_id = r.mpa_rating_id";


    private final String insertQuery =
            "INSERT INTO films(name, description, release_date, duration, mpa_rating_id)" +
                    "VALUES(?, ?, ?, ?, ?)";

    private final String updateQuery =
            "UPDATE films SET " +
                    "name = ?, " +
                    "description = ?, " +
                    "release_date = ?, " +
                    "duration = ?, " +
                    "mpa_rating_id = ? " +
                    "WHERE film_id = ?";

    private final String getByIdQuery =
            "SELECT f.*, r.mpa_name " +
                    "FROM films AS f JOIN mpa_rating AS r ON f.mpa_rating_id = r.mpa_rating_id " +
                    "WHERE f.film_id = ? ";

    private final String addLikeQuery = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";

    private final String deleteLikeQuery =
            "DELETE FROM likes WHERE " +
                    "film_id = ? AND user_id = ?";

    private final String getPopularQuery =
            "SELECT f.*, mr.mpa_name " +
                    "FROM films AS f " +
                    "JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id " +
                    "LEFT OUTER JOIN likes l ON (f.film_id = l.film_id) " +
                    "GROUP BY f.film_id, mr.mpa_name " +
                    "ORDER BY COUNT(l.USER_ID) desc " +
                    "LIMIT ?";

    private final String setGenreQuery = "INSERT INTO films_genres (genre_id, film_id) VALUES(?, ?)";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreDbStorage genreDbStorage, MapRatingDbService mapRatingDbService) {
        super(jdbc, mapper);
        this.genreDbStorage = genreDbStorage;
        this.filmValidate = new FilmValidate();
        this.mapRatingDbService = mapRatingDbService;
    }

    @Override
    public Film create(Film film) {

        MpaRating mpa = mapRatingDbService.getMpaRating(film.getMpa().getId());
        film.setMpa(mpa);

        long id = insert(
                insertQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        filmValidate.validRelease(film);
        log.info("Film {} успешно прошел валидацию на дату.", film.getName());

        if (Objects.nonNull(film.getGenres())) {
            List<Genre> genres = film.getGenres();
            Set<Genre> list = genres.stream()
                    .map(genre -> genreDbStorage.getGenreById(genre.getId()))
                    .collect(Collectors.toSet());
            list.stream().map(Genre::getId).forEach(genreId -> update(setGenreQuery, genreId, film.getId()));
        }
        log.info("Film {} успешно записан.", film.getName());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        update(
                updateQuery,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getMpa().getId(),
                newFilm.getId()
        );
        log.info("Film {} успешно обновлен.", newFilm.getName());
        return newFilm;
    }

    @Override
    public Collection<Film> getAll() {
        log.info("Получение списка фильмов.");
        return findMany(getAllQuery);
    }

    @Override
    public Film getById(Long filmId) {
        log.info("Получение фильма с id {}.", filmId);
        Film film = findOne(getByIdQuery, filmId);
        List<Genre> genres = genreDbStorage.getGenresFilmById(filmId);
        film.setGenres(genres);
        log.info("Film {} успешно получен.>.", film.getName());
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        log.info("Добавление лайка фильму с id {} от пользователя с id {}.", filmId, userId);
        update(
                addLikeQuery,
                filmId,
                userId
        );
        log.info("Лайк успешно добавлен для фильма с id {} от пользователя c id {}", filmId, userId);
        return findOne(getByIdQuery, filmId);
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        log.info("Удаление лайка у фильма с id {} от пользователя с id {}.", filmId, userId);
        boolean success = delete(
                deleteLikeQuery,
                filmId,
                userId
        );
        if (success) {
            log.info("Удаление лайка у фильма с id {} от пользователя c id {} прошло успешно.", filmId, userId);
            return findOne(getByIdQuery, filmId);
        } else {
            log.warn("Произошла ошибка при удалении лайка у фильма с id {}.", filmId);
            throw new InternalServerException("Удаление не удалось.");
        }
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        log.info("Получение списка популярных фильма количеством {}.", count);
        return findMany(getPopularQuery, count);
    }

}

