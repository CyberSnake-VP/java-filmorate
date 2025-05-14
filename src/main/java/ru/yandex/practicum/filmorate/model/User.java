package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class User {
    @Positive(message = "Id должен быть положительным числом.")
    private Long id;

    @NotEmpty(message = "Email должен быть указан.")
    @Email(message = "Email не заполнен или не корректен.")
    private String email;


    @NotEmpty(message = "Логин должен быть заполнен.")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы.")
    private String login;

    private String name;

    @Past(message = "Дата рождения не может быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    // Список друзей
    private Set<Long> friends;
}
