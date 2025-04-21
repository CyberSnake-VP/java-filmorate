package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validate.UserValidate;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage{
    // Таблица для хранения наших пользователей по email
    private final Map<Long, User> users = new HashMap<>();
    private final UserValidate validate;

    // Создание и добавление пользователя в таблицу, при прохождении валидации. Если имя не указано, будет записан логин.
    @Override
    public User create(User user) {
        log.info("Добавление пользователя {}.", user);
        log.debug("Проверка пользователя с email {} на повторное добавление.", user.getEmail());
        validate.isRepeat(users, user);
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

        log.debug("Валидация на Null c id {}", id);
        validate.isNull(id);
        log.debug("Проверка id {} на существование в таблице.", id);
        validate.isExist(users.containsKey(id), id);

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

    @Override
    public User getById(Long userId) {
       validate.isExist(users.containsKey(userId), userId);
       return users.get(userId);
    }
}
