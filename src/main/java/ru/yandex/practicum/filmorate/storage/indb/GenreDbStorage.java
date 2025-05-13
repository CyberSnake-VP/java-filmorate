package ru.yandex.practicum.filmorate.storage.indb;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

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
}
