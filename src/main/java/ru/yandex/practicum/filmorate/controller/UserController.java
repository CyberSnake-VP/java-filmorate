package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    // Таблица для хранения наших пользователей по email
    Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        log.info("Список пользователей успешно отправлен");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Добавление пользователя {}.", user);
        validateToCreate(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен в список");
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        Long id = newUser.getId();
        if (Objects.isNull(id)) {
            String message = "id должен быть указан.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (!users.containsKey(id)) {
            String message = String.format("Пользователь с id %d, не найден.", newUser.getId());
            log.warn(message);
            throw new NotFoundException(message);
        }

        User oldUser = users.get(newUser.getId());

        if (Objects.nonNull(newUser.getEmail())) {
            if (!isValidEmail(newUser)) {
                emailException();
            }
            oldUser.setEmail(newUser.getEmail());
        }
        if (Objects.nonNull(newUser.getLogin())) {
            if (!isValidLogin(newUser)) {
                loginException();
            }
            oldUser.setLogin(newUser.getLogin());
        }
        if (Objects.nonNull(newUser.getBirthday())) {
            if (!isValidBirthDay(newUser)) {
                birthDayException();
            }
            oldUser.setBirthday(newUser.getBirthday());
        }
        if (Objects.nonNull(newUser.getName())) {
            oldUser.setName(newUser.getName());
        }

        users.put(id, oldUser);
        log.info("Данные пользователя с id {} успешно обновлены.", id);
        return oldUser;
    }

    // метод для получения id, получаем максимальный ключ-id нашей таблицы пользователей, увеличиваем его на 1
    // если это первый ключ, то записываем значение 1
    private Long getNextId() {
        long nextId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++nextId;
    }

    private void validateToCreate(User user) {
        if (!isValidEmail(user)) {
            emailException();
        }
        if (!isValidLogin(user)) {
            loginException();
        }
        if (!isValidBirthDay(user)) {
            birthDayException();
        }
        if (users.containsValue(user)) {
            duplicateEmailException();
        }
        if (Objects.isNull(user.getName())) {
            log.info("Пользователь с id не указал, установка логина в качестве имени.");
            user.setName(user.getLogin());
        }
        log.info("Валидация пользователя с id {} прошла успешна. ", user.getId());
    }

    private void emailException() {
        String message = "Имейл не указан или некорректен.";
        log.warn(message);
        throw new ValidationException(message);
    }

    private void loginException() {
        String message = "Логин должен быть указан и не должен содержать пробелы.";
        log.warn(message);
        throw new ValidationException(message);
    }

    private void birthDayException() {
        String message = "Дата рождения указана неверно.";
        log.warn(message);
        throw new ValidationException(message);
    }

    private void duplicateEmailException() {
        String message = "Этот имейл уже используется";
        log.warn(message);
        throw new DuplicatedDataException(message);
    }


    private boolean isValidBirthDay(User user) {
        if (Objects.isNull(user.getBirthday())) {
            return true;
        }
        return user.getBirthday().isBefore(LocalDate.now());
    }

    private boolean isValidLogin(User user) {
        return Objects.nonNull(user.getLogin()) && !user.getLogin().contains(" ");
    }

    private boolean isValidEmail(User user) {
        return Objects.nonNull(user.getEmail()) && !user.getEmail().isBlank() && user.getEmail().contains("@");
    }

}
