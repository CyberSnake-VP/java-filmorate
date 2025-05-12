package ru.yandex.practicum.filmorate.storage.inmemory;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validate.UserValidate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Qualifier("InMemoryUserStorage")
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
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
     * Метод для обновления, проверяем id, на null и в качестве ключа к таблице.
     * Получаем из таблицы пользователя, меняем ему поля, если поля были указны в Json и прошли валидацию.
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
        log.info("Получение пользователя по id {}", userId);
        validate.isExist(users.containsKey(userId), userId);
        return users.get(userId);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        log.info("Добавление пользователей в друзья.");
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Попытка добавить в список друзей самого себя.");
        }

        User user = getById(userId);
        User userFriend = getById(friendId);
        log.debug("Пользователи существуют.");
        user.getFriends().add(friendId);
        userFriend.getFriends().add(userId);
        return user;
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        log.info("Удаление пользователя из друзей.");
        log.debug("Получаем пользователей.");
        User user = getById(userId);
        User userFriend = getById(friendId);

        // Если у пользователя, нет данного друга, то просто возвращаем объект пользователя.
        if (!user.getFriends().contains(userFriend.getId())) {
            return user;
        }
        log.debug("Удаление из списков друзей.");
        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(userId);
        return user;
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        log.info("Получение списка друзей пользователя.");
        log.debug("Получение пользователя.");
        User user = getById(userId);
        log.debug("Получаем список id пользователей.");
        Set<Long> idSet = user.getFriends();
        if (idSet.isEmpty()) {
            log.debug("Список пользователя пуст, отправляем пустой список.");
            return List.of();
        }
        log.debug("Возвращаем список друзей пользователя.");
        return idSet.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Получение списка общих друзей пользователя.");
        log.debug("Получаем пользователя.");
        User user = getById(userId);
        User ohterUser = getById(otherId);
        log.debug("Получение списка id пользователей.");
        Set<Long> idSetUser = user.getFriends();
        Set<Long> idSetOther = ohterUser.getFriends();
        if (idSetUser.isEmpty() || idSetOther.isEmpty()) {
            log.debug("Отправляем пустой список.");
            return List.of();
        }
        log.debug("Создание множества и получение пересечения.");
        Set<Long> commonFriends = new HashSet<>(idSetUser);
        commonFriends.retainAll(idSetOther);
        if (commonFriends.isEmpty()) {
            log.debug("Нет пересечения, отправляем пустой список.");
            return List.of();
        }
        log.debug("Возвращаем список общих друзей пользователя.");
        return commonFriends.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
}
