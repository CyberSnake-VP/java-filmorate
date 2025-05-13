package ru.yandex.practicum.filmorate.service.indb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaRatingDbService implements MpaRatingService {
    private final MpaRatingStorage mpaRatingStorage;

    @Override
    public MpaRating getMpaRating(int id) {
        return mpaRatingStorage.getMpaRating(id);
    }

    @Override
    public List<MpaRating> getAllMpaRatings() {
        return mpaRatingStorage.getAllMpaRatings();
    }
}
