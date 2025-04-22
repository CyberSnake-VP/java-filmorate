package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Collection;


@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Slf4j
@Validated
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.getAll();
    }

    @GetMapping("{filmId}")
    public Film getById(@PathVariable(value = "filmId", required = false) @Min(1)  Long filmId) {
        return filmService.getById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(value = "count", defaultValue = "10") @Min(1)  Long count) {
        return filmService.getPopular(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmService.update(newFilm);
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLike(@PathVariable("id") @Min(1)  Long id,
                        @PathVariable("userId") @Min(1)  Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLike(@PathVariable("id") @Min(1)  Long id,
                           @PathVariable("userId") @Min(1)  Long userId) {
        return filmService.deleteLike(id, userId);
    }

}
