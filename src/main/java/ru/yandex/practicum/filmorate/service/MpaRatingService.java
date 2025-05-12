package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingService {
    MpaRating getMpaRating(int id);

    List<MpaRating> getAllMpaRatings();
}
