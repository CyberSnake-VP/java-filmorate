package ru.yandex.practicum.filmorate.service.inmemory;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Qualifier("UserServiceImplement")
public class UserServiceImplement implements UserService {
    // Подключаем хранилище для работы с сервисом. В хранилище логика по добавлению, обновлению, поиску и удалению.

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImplement(@Qualifier("InMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
      return userStorage.addFriend(userId, friendId);
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
      return userStorage.getAllFriends(userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        return userStorage.getCommonFriends(userId, otherId);
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
