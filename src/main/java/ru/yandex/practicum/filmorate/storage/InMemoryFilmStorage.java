package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    // таблица для хранения фильмов
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        log.info("Добавление фильма {}", film);
        validRelease(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id {} успешно добавлен.", film.getId());
        return film;
    }

    /**
     Метод для обновления, проверяем id, на null и в качестве ключа к таблице.
     Получаем из таблицы фильм, меняем ему поля, если поля были указны в Json и прошли валидацию.
     */
    @Override
    public Film update(Film newFilm) {
        log.info("Обновление данных фильма {} c id {}", newFilm, newFilm.getId());
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
        log.info("Данные фильма с id {} успешны обновлены.", oldFilm.getId());
        return oldFilm;
    }

    @Override
    public Collection<Film> getAll() {
        log.debug("Список фильма успешно отправлен.");
        return films.values();
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

    private Long getNextId() {
        long nextId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++nextId;
    }
}
