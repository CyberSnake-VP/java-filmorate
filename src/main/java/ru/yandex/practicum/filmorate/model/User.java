package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class User {
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
}
