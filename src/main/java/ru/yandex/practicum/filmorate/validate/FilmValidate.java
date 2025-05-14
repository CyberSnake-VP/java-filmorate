package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.ValidationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Objects;

@Component
@Slf4j
@Data
public class FilmValidate implements Validate {

    private final LocalDate validReleaseDate = LocalDate.of(1895, 12, 28);

    @Override
    public void isNull(Long id) {
        if (Objects.isNull(id)) {
            String message = "id должен быть указан.";
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    @Override
    public void isExist(boolean existing, Long id) {
        if (!existing) {
            String message = String.format("Фильм с id %d, не найден.", id);
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    // Функциональность для метода валидации validRelease
    public boolean isValidRelease(Film film) {
        return Objects.isNull(film.getReleaseDate()) || film.getReleaseDate().isAfter(validReleaseDate);
    }

    // Валидация даты релиза.
    public void validRelease(Film film) {
        if (!isValidRelease(film)) {
            String message = "Дата выходы не может быть раньше 1895-12-28";
            log.warn(message);
            throw new ValidationException(message);
        }
    }
}
