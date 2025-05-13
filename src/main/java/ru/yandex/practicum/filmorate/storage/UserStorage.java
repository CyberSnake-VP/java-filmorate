package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User newUser);

    Collection<User> getAll();

    User getById(Long userId);

    User addFriend(Long userId, Long friendId);

    User deleteFriend(Long userId, Long friendId);

    Collection<User> getAllFriends(Long userId);

    Collection<User> getCommonFriends(Long userId, Long otherId);
}
