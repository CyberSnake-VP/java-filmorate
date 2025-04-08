package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
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
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма. {}", film);
        processCreate(film);
        log.info("Фильм с id {} успешно добавлен.", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Обновление данных фильма {} c id {}", newFilm, newFilm.getId());
        Film oldFilm = processUpdate(newFilm);
        log.info("Данные фильма с id {} успешны обновлены.", oldFilm.getId());
        return oldFilm;
    }

    private void processCreate(@Valid Film film) {
        validRelease(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
    }
    /** Метод для обновления, проверяем id, на null и в качестве ключа к таблице.
     Получаем из таблицы фильм, меняем ему поля, если поля были указны в Json и прошли валидацию.*/
    private Film processUpdate(@Valid Film newFilm) {
        Long id = newFilm.getId();
        if (Objects.isNull(id)) {
            String message = "id должен быть указан.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (!films.containsKey(id)) {
            String message = String.format("Фильм с id %d, не найден.", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
        // Проверяем дату релиза, чтобы не раньше 1895-12-28 числа.
        validRelease(newFilm);

        Film oldFilm = films.get(id);

        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        films.put(id, oldFilm);
        return oldFilm;
    }

    private Long getNextId() {
        long nextId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++nextId;
    }

    // Функциональность для метода валидации validRelease
    private boolean isValidRelease(Film film) {
        return Objects.isNull(film.getReleaseDate()) || film.getReleaseDate().isAfter(LocalDate.of(1895, 12, 28));
    }

    // Валидация даты релиза.
    private void validRelease(Film film) {
        if (!isValidRelease(film)) {
            String message = "Дата выходы не может быть раньше 1895-12-28";
            log.warn(message);
            throw new ValidationException(message);
        }
    }

}
