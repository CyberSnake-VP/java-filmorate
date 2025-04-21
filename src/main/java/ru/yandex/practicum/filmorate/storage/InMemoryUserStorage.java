package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage{
    // Таблица для хранения наших пользователей по email
    private final Map<Long, User> users = new HashMap<>();

    // Создание и добавление пользователя в таблицу, при прохождении валидации. Если имя не указано, будет записан логин.
    @Override
    public User create(User user) {
        log.info("Добавление пользователя {}.", user);
        user.setId(getNextId());
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно добавлен в список");
        return user;
    }

    /**
     Метод для обновления, проверяем id, на null и в качестве ключа к таблице.
     Получаем из таблицы пользователя, меняем ему поля, если поля были указны в Json и прошли валидацию.
     */
    @Override
    public User update(User newUser) {
        log.info("Обновление данных пользователя {} c id {}", newUser, newUser.getId());
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

        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null) {
            oldUser.setBirthday(newUser.getBirthday());
        }

        users.put(id, oldUser);
        log.info("Данные пользователя с id {} успешно обновлены.", newUser.getId());
        return oldUser;
    }

    @Override
    public Collection<User> getAll() {
        log.debug("Список пользователей успешно отправлен");
        return users.values();
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
}
