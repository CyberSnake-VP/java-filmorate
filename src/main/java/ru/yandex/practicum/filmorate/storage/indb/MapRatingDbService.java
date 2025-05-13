package ru.yandex.practicum.filmorate.storage.indb;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Repository
public class MapRatingDbService extends BaseDbStorage<MpaRating> implements MpaRatingStorage {

    private final String GET_BY_ID_QUERY = "SELECT * FROM mpa_rating WHERE mpa_rating_id = ?";
    private final String GET_ALL_QUERY = "SELECT * FROM mpa_rating ORDER BY mpa_rating_id";

    public MapRatingDbService(JdbcTemplate jdbc, RowMapper<MpaRating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public MpaRating getMpaRating(int id) {
        return findOne(GET_BY_ID_QUERY, id);
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        return findMany(GET_ALL_QUERY);
    }
}
