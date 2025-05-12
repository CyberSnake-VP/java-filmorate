package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaRatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
@Slf4j
@Validated
public class MpaRatingController {
    private final MpaRatingService mpaRatingService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public MpaRating findById(@PathVariable("id") int id) {
        return mpaRatingService.getMpaRating(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MpaRating> getMpaRatings() {
        return mpaRatingService.getAllMpaRatings();
    }
}
