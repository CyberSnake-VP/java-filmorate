package ru.yandex.practicum.filmorate.storage.indb;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.validate.FilmValidate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private final GenreDbStorage genreDbStorage;
    private final FilmValidate filmValidate;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreDbStorage genreDbStorage) {
        super(jdbc, mapper);
        this.genreDbStorage = genreDbStorage;
        this.filmValidate = new FilmValidate();
    }

    private final String GET_ALL_QUERY = "SELECT * FROM films";


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

    private final String GET_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";

    private final String ADD_LIKE_QUERY = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";

    private final String DELETE_LIKE_QUERY =
            "DELETE FROM likes WHERE " +
                    "film_id = ? AND user_id = ?";

    private final String GET_POPULAR_FILM =
            "SELECT f.film_id, " +
                    "f.name, " +
                    "f.description, " +
                    "f.release_date, " +
                    "f.duration, " +
                    "f.mpa_rating_id," +
                    "count(l.user_id) as l_count " +
                    "from FILMS f " +
                    "join MPA_RATING m ON f.MPA_RATING_ID = m.MPA_RATING_ID " +
                    "left outer join LIKES l On f.FILM_ID = l.FILM_ID " +
                    "GROUP BY f.film_id " +
                    "ORDER BY l_count desc " +
                    "LIMIT ?";

    private final String SET_GENRE_QUERY = "INSERT INTO films_genres (genre_id, film_id) VALUES(?, ?)";

    @Override
    public Film create(Film film) {
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
        if (Objects.nonNull(film.getGenres())) {
            List<Genre> genres = film.getGenres();
            List<Genre> list = genres.stream()
                    .map(genre -> genreDbStorage.getGenreById(genre.getId()))
                    .toList();
            list.stream().map(Genre::getId).forEach(genreId -> update(SET_GENRE_QUERY, genreId, film.getId()));

            genres.clear();
            genres.addAll(list);
            System.out.println();
        }
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
        return newFilm;
    }

    @Override
    public Collection<Film> getAll() {
        return findMany(GET_ALL_QUERY);
    }

    @Override
    public Film getById(Long filmId) {
        return findOne(GET_BY_ID_QUERY, filmId);
    }

    @Override
    public Film addLike(Long film_id, Long userId) {
        update(
                ADD_LIKE_QUERY,
                film_id,
                userId
        );
        return findOne(GET_BY_ID_QUERY, film_id);
    }

    @Override
    public Film deleteLike(Long film_id, Long userId) {
        boolean success = delete(
                DELETE_LIKE_QUERY,
                film_id,
                userId
        );
        if (success) {
            return findOne(GET_BY_ID_QUERY, film_id);
        } else {
            throw new InternalServerException("Удаление не удалось.");
        }
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        return findMany(GET_POPULAR_FILM, count);
    }

}

