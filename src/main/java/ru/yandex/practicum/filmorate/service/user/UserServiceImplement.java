package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validate.UserValidate;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {
    // Подключаем хранилище для работы с сервисом. В хранилище логика по добавлению, обновлению, поиску и удалению.
    private final UserStorage userStorage;
    private final UserValidate validate;

    @Override
    public User create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User getById(Long userId) {
        return userStorage.getById(userId);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        log.info("Добавление пользователей в друзья.");
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Попытка добавить в список друзей самого себя.");
        }

        User user = userStorage.getById(userId);
        User userFriend = userStorage.getById(friendId);
        log.debug("Пользователи существуют.");
        user.getFriends().add(friendId);
        userFriend.getFriends().add(userId);
        return user;
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        log.info("Удаление пользователя из друзей.");
        log.debug("Получаем пользователей.");
        User user = userStorage.getById(userId);
        User userFriend = userStorage.getById(friendId);

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
        User user = userStorage.getById(userId);
        log.debug("Получаем список id пользователей.");
        Set<Long> idSet = user.getFriends();
        if (idSet.isEmpty()) {
            log.debug("Список пользователя пуст, отправляем пустой список.");
            return List.of();
        }
        log.debug("Возвращаем список друзей пользователя.");
        return idSet.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        log.info("Получение списка общих друзей пользователя.");
        log.debug("Получаем пользователя.");
        User user = userStorage.getById(userId);
        User ohterUser = userStorage.getById(otherId);
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
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    @Override
    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }
}
