package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
    public User create(@Valid @RequestBody User user) {
        processCreate(user);
        log.info("Пользователь успешно добавлен в список");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Обновление данных пользователя {} c id {}", newUser, newUser.getId());
        User oldUser = processUpdate(newUser);
        log.info("Данные пользователя с id {} успешно обновлены.", newUser.getId());
        return oldUser;
    }

    // Метод для получения id, получаем максимальный ключ-id нашей таблицы пользователей, увеличиваем его на 1
    // если это первый ключ, то записываем значение 1
    private Long getNextId() {
        long nextId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++nextId;
    }

    // Создание и добавление пользователя в таблицу, при прохождении валидации. Если имя не указано, будет записан логин.
    private void processCreate(@Valid User user) {
        log.info("Добавление пользователя {}.", user);
        user.setId(getNextId());
        if(Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
    }

    private User processUpdate(@Valid User newUser) {
        Long id = newUser.getId();
        if (Objects.isNull(id)) {
            String message = "id должен быть указан.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (!users.containsKey(id)) {
            String message = String.format("Пользователь с id %d, не найден.", id);
            log.warn(message);
            throw new NotFoundException(message);
        }

        User oldUser = users.get(id);

        if(newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if(newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if(newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if(newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }

        users.put(id, oldUser);
        return oldUser;
    }

}
