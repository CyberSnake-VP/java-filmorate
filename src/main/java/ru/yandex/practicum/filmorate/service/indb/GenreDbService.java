package ru.yandex.practicum.filmorate.service.indb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreDbService implements GenreService {
    private final GenreStorage genreStorage;

    @Override
    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id);
    }

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }
}
