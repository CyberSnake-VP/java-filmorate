package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Data
public class Film {
    @Positive(message = "Id должен быть положительным числом.")
    private Long id;

    @NotEmpty(message = "Название не может быть пустым.")
    private String name;

    @Size(max = 200)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом.")
    private Integer duration;

    // количество лайков
    private List<Long> likes;

    // жанр фильма
    private List<Genre> genres;

    // рейтинг фильма
    @NotNull
    private MpaRating mpa;
}

