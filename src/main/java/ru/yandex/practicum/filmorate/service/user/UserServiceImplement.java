package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService{
    // Подключаем хранилище для работы с сервисом. В хранилище логика по добавлению, обновлению, поиску и удалению.
    private final UserStorage userStorage;
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
        log.debug("Добавление пользователей в друзья.");
        if(Objects.equals(userId, friendId)){
            throw new ValidationException("Попытка добавить в список друзей самого себя.");
        }

        User user  = userStorage.getById(userId);
        User userFriend = userStorage.getById(friendId);
        log.debug("Пользователи существуют.");
        user.getFriends().add(friendId);
        userFriend.getFriends().add(userId);
        return user;
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
