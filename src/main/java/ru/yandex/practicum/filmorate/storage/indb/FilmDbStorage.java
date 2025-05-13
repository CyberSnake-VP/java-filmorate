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

    private final String GET_ALL_QUERY =
            "SELECT f.*, r.mpa_name " +
                    "FROM films AS f JOIN mpa_rating AS r ON f.mpa_rating_id = r.mpa_rating_id";


    private final String INSERT_QUERY =
            "INSERT INTO films(name, description, release_date, duration, mpa_rating_id)" +
                    "VALUES(?, ?, ?, ?, ?)";

    private final String UPDATE_QUERY =
            "UPDATE films SET " +
                    "name = ?, " +
                    "description = ?, " +
                    "release_date = ?, " +
                    "duration = ?, " +
                    "mpa_rating_id = ? " +
                    "WHERE film_id = ?";

    private final String GET_BY_ID_QUERY =
            "SELECT f.*, r.mpa_name " +
                    "FROM films AS f JOIN mpa_rating AS r ON f.mpa_rating_id = r.mpa_rating_id " +
                    "WHERE f.film_id = ? ";

    private final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";

    private final String DELETE_LIKE_QUERY =
            "DELETE FROM likes WHERE " +
                    "film_id = ? AND user_id = ?";

    private final String GET_POPULAR_FILM =
            "SELECT f.*, mr.mpa_name " +
                    "FROM films AS f " +
                    "JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id " +
                    "LEFT OUTER JOIN likes l ON (f.film_id = l.film_id) " +
                    "GROUP BY f.film_id, mr.mpa_name " +
                    "ORDER BY COUNT(l.USER_ID) desc " +
                    "LIMIT ?";

    private final String SET_GENRE_QUERY = "INSERT INTO films_genres (genre_id, film_id) VALUES(?, ?)";


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
                INSERT_QUERY,
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
            list.stream().map(Genre::getId).forEach(genreId -> update(SET_GENRE_QUERY, genreId, film.getId()));
        }
        log.info("Film {} успешно записан.", film.getName());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        update(
                UPDATE_QUERY,
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
        return findMany(GET_ALL_QUERY);
    }

    @Override
    public Film getById(Long filmId) {
        log.info("Получение фильма с id {}.", filmId);
        Film film = findOne(GET_BY_ID_QUERY, filmId);
        List<Genre> genres = genreDbStorage.getGenresFilmById(filmId);
        film.setGenres(genres);
        log.info("Film {} успешно получен.>.", film.getName());
        return film;
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        log.info("Добавление лайка фильму с id {} от пользователя с id {}.", filmId, userId);
        update(
                ADD_LIKE_QUERY,
                filmId,
                userId
        );
        log.info("Лайк успешно добавлен для фильма с id {} от пользователя c id {}", filmId, userId);
        return findOne(GET_BY_ID_QUERY, filmId);
    }

    @Override
    public Film deleteLike(Long filmId, Long userId) {
        log.info("Удаление лайка у фильма с id {} от пользователя с id {}.", filmId, userId);
        boolean success = delete(
                DELETE_LIKE_QUERY,
                filmId,
                userId
        );
        if (success) {
            log.info("Удаление лайка у фильма с id {} от пользователя c id {} прошло успешно.", filmId, userId);
            return findOne(GET_BY_ID_QUERY, filmId);
        } else {
            log.warn("Произошла ошибка при удалении лайка у фильма с id {}.", filmId);
            throw new InternalServerException("Удаление не удалось.");
        }
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        log.info("Получение списка популярных фильма количеством {}.", count);
        return findMany(GET_POPULAR_FILM, count);
    }

}

