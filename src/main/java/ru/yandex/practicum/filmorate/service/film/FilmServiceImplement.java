package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImplement implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film create(Film film) {
        return filmStorage.create(film);
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
        return filmStorage.getById(filmId);
    }

    @Override
    public Film addLike(Long id, Long userId) {
        log.info("Пользователь с id {} ставит лайк.", userId);
        log.debug("Проверяем есть ли пользователь.");
        userStorage.getById(userId);
        Film film = filmStorage.getById(id);
        log.debug("Проверяем ставил ли пользователь лайк.");
        if (film.getLikes().contains(userId)) {
            String errorString = String.format("Пользователь c id %d уже поставил лайк данному фильму.", userId);
            log.warn(errorString);
            throw new ValidationException(errorString);
        }
        log.debug("Добавляем лайк.");
        film.getLikes().add(userId);

        return film;
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        log.info("Пользователь с id {} удаляет лайк.", userId);
        userStorage.getById(userId);
        Film film = filmStorage.getById(id);
        log.debug("Проверяем существует ли лайк, от пользователя.");
        if (!film.getLikes().contains(userId)) {
            String errorString = String.format("Пользователь c id %d не ставил лайк данному фильму.", userId);
            log.warn(errorString);
            throw new ValidationException(errorString);
        }
        log.debug("Удаляем лайк.");
        film.getLikes().remove(userId);

        return film;
    }

    @Override
    public Collection<Film> getPopular(Long count) {
        log.info("Получение списка {} популярных фильмов.", count);

        // Используем интерфейс компаратор, для сортировки по количеству лайков. Ограничиваем кол-во указанным count
        List<Film> films = filmStorage.getAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size()))
                .toList();
        log.debug("Возвращаемое количество фильмов {}", count);
        return films.reversed().stream()
                .limit(10)
                .toList();
    }
}
