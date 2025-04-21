package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

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
    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    @Override
    public Collection<User> getAll() {
        return userStorage.getAll();
    }
}
