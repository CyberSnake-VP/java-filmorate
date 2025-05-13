package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
@Slf4j
@Validated
public class GenreController {
    private final GenreService genreService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Genre findById(@PathVariable("id") int id) {
        return genreService.getGenreById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }
}
