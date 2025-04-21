package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validate.FilmValidate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    // таблица для хранения фильмов
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidate validate;

    @Override
    public Film create(Film film) {
        log.info("Добавление фильма {}", film);
        validate.validRelease(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id {} успешно добавлен.", film.getId());
        return film;
    }


    @Override
    public Film update(Film newFilm) {
        log.info("Обновление данных фильма {} c id {}", newFilm, newFilm.getId());
        Long id = newFilm.getId();

        log.debug("Валидация на Null c id {}", id);
        validate.isNull(id);
        log.debug("Проверка id {} на существование в таблице.", id);
        validate.isExist(films.containsKey(id), id);
        log.debug("Проверка фильма c датой {} на дату выхода {}", newFilm.getReleaseDate(), validate.getValidReleaseDate());
        validate.validRelease(newFilm);

        // Получаем из таблицы фильм, меняем ему поля, если поля были указны в Json и прошли валидацию.
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

    @Override
    public Film getById(Long filmId) {
        log.debug("Получение пользователя по id {}", filmId);
        validate.isExist(films.containsKey(filmId), filmId);
        log.info("Пользователь успешно получен.");
        return films.get(filmId);
    }

    private Long getNextId() {
        long nextId = films.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++nextId;
    }

}
