package ru.yandex.practicum.filmorate.storage.indb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private final String getByIdQuery = "SELECT * FROM genres WHERE genre_id = ?";

    private final String getAllQuery = "SELECT * FROM genres ORDER BY genre_id ";

    private final String getGenreFilm =
            "SELECT g.genre_id, g.genre_name " +
                    "FROM films AS f " +
                    "JOIN films_genres AS fg ON (fg.film_id = f.FILM_ID) " +
                    "JOIN genres AS g ON(g.genre_id = fg.genre_id) " +
                    "WHERE f.FILM_ID = ?";

    private final String setGenreQuery = "INSERT INTO films_genres (genre_id, film_id) VALUES(?, ?)";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Genre getGenreById(int id) {
        return findOne(getByIdQuery, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return findMany(getAllQuery);
    }

    @Override
    public List<Genre> getGenresFilmById(Long id) {
        return findMany(getGenreFilm, id);
    }

    @Override
    public void setGenresToFilm(Long filmId, List<Genre> genres) {
        log.info("Запись жанров в фильм {}", filmId);
        // Фильтруем жанры на уникальные id
        Set<Integer> genresSet = new HashSet<>();
        for (Genre genre : genres) {
            genresSet.add(genre.getId());
        }
        List<Object[]> params = new ArrayList<>();
        genresSet.forEach(genreId ->
            params.add(new Object[]{genreId, filmId})  // Подготавливаем массив объектов для butchUpdate
        );

        jdbc.batchUpdate(setGenreQuery, params);
        log.info("Жанры успешно добавлены в фильм с id {}", filmId);
    }

}
