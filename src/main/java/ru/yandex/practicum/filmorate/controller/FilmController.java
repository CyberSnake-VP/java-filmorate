package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    // таблица для хранения фильмов
    Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Добавление фильма. {}", film);
        validateForCreate(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id {} успешно добавлен.", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        Long id = newFilm.getId();
        if (Objects.isNull(id)) {
            String message = "id должен быть указан.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (!films.containsKey(id)) {
            String message = String.format("Фильм с id %d, не найден.", newFilm.getId());
            log.warn(message);
            throw new NotFoundException(message);
        }

        Film oldFilm = films.get(newFilm.getId());

        if (Objects.nonNull(newFilm.getName())) {
            if (!isValidName(newFilm)) {
                nameException();
            }
            oldFilm.setName(newFilm.getName());
        }
        if (Objects.nonNull(newFilm.getDescription())) {
            if (!isValidDescription(newFilm)) {
                descriptionException();
            }
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (Objects.nonNull(newFilm.getReleaseDate())) {
            if (!isValidRelease(newFilm)) {
                releaseException();
            }
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (Objects.nonNull(newFilm.getDuration())) {
            if (!isValidDuration(newFilm)) {
                durationException();
            }
            oldFilm.setDuration(newFilm.getDuration());
        }
        log.info("Данные фильма с id {} успешны обновлены.", oldFilm.getId());
        return oldFilm;
    }


    private Long getNextId() {
        long nextId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++nextId;
    }

    private void validateForCreate(Film film) {
        if (!isValidName(film)) {
            nameException();
        }
        if (!isValidDescription(film)) {
            descriptionException();
        }
        if (!isValidRelease(film)) {
            releaseException();
        }
        if (!isValidDuration(film)) {
            durationException();
        }
        log.info("Фильм {} прошел валидацию.", film);
    }

    private void nameException() {
        String message = "Название должно быть заполнено.";
        log.warn(message);
        throw new ValidationException(message);
    }

    private void descriptionException() {
        String message = "Описание не должно превышать 200 символов.";
        log.warn(message);
        throw new ValidationException(message);
    }

    private void releaseException() {
        String message = "Дата создания указана неверно.";
        log.warn(message);
        throw new ValidationException(message);
    }

    private void durationException() {
        String message = "Продолжительность должна быть больше 0.";
        log.warn(message);
        throw new ValidationException(message);
    }

    private boolean isValidName(Film film) {
        return Objects.nonNull(film.getName()) && !film.getName().isBlank();
    }

    private boolean isValidDescription(Film film) {
        int numberOfChar = 200;
        return (Objects.isNull(film.getDescription())) ? true : film.getDescription().length() <= numberOfChar;
    }

    private boolean isValidRelease(Film film) {
        return (Objects.isNull(film.getReleaseDate())) ? true : film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28));
    }

    private boolean isValidDuration(Film film) {
        return (Objects.isNull(film.getDuration())) ? true : film.getDuration() > 0;
    }

}
