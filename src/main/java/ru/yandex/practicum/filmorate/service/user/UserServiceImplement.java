package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validate.UserValidate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {
    // Подключаем хранилище для работы с сервисом. В хранилище логика по добавлению, обновлению, поиску и удалению.
    private final UserStorage userStorage;
    private final UserValidate validate;
    private final FilmStorage filmStorage;

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
        log.debug("Проверяем существуют ли пользователи в списках друзей друг друга.");
        validate.isRepeatFriend(user, friendId);
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
        if(idSet.isEmpty()) {
            log.debug("Список пользователя пуст, отправляем пустой список.");
            return List.of();
        }
        log.debug("Возвращаем список друзей пользователя.");
        return idSet.stream()
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
