package ru.yandex.practicum.filmorate.storage.indb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;

@Repository
@Qualifier("UserDbStorage")
@Slf4j
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {
    private final String getAllQuery = "SELECT * FROM users";

    private final String insertQuery =
            "INSERT INTO users(email, login, name, birthday)" +
                    "VALUES(?, ?, ?, ?)";

    private final String updateQuery =
            "UPDATE users SET " +
                    "login = ?, " +
                    "name = ?, " +
                    "email = ?, " +
                    "birthday = ? " +
                    "WHERE user_id = ?";

    private final String getByIdQuery = "SELECT * FROM users WHERE user_id = ?";

    private final String insertFriendsQuery =
            "INSERT INTO friends (friend_id, user_id)" +
                    "VALUES (?, ?)";

    private final String deleteFriendsQuery =
            "DELETE FROM friends WHERE " +
                    "(friend_id = ? AND user_id = ?)";

    private final String getAllFriendsQuery =
            "SELECT * FROM users " +
                    "WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";

    private final String getCommonFriendsQuery =
            " SELECT * FROM USERS " +
                    "WHERE user_id IN ( " +
                    "SELECT f.friend_id " +
                    "FROM friends AS f " +
                    "WHERE f.user_id IN (?, ?) " +
                    "GROUP BY f.friend_id " +
                    "HAVING COUNT(*) > 1 " +
                    ")";

    //Инициализация базового репозитория
    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User create(User user) {
        log.info("Создание нового пользователя с id: {}", user.getId());
        long id = insert(
                insertQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.info("пользователь с id {} успешно создан.", id);
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Обновление данных пользователя с id {}", user.getId());
        update(
                updateQuery,
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        log.info("успешное обновление данных пользователя с id {}.", user.getId());
        return user;
    }

    @Override
    public Collection<User> getAll() {
        log.info("Получение списка пользователей.");
        return findMany(getAllQuery);
    }

    @Override
    public User getById(Long userId) {
        log.info("Получение пользователя с id {}", userId);
        return findOne(getByIdQuery, userId);
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        log.info("Добавление в друзья к пользователя с id {}, пользователя с id {}", userId, friendId);
        User user = findOne(getByIdQuery, userId);
        User friend = findOne(getByIdQuery, friendId);

        // пользователь не должен добавить самого себя в друзья
        if (!Objects.equals(user.getId(), friend.getId())) {
            update(
                    insertFriendsQuery,
                    friendId,
                    userId
            );
            log.info("Добавление в друзья прошло успешно, у пользователя с id {}.", userId);
            return findOne(getByIdQuery, friendId);
        } else {
            log.warn("Ошибка при добавлении в друзья у пользователя с  id {}.", userId);
            throw new NotFoundException("Попытка добавить в друзья самого себя.");
        }
    }

    @Override
    public User deleteFriend(Long userId, Long friendId) {
        log.info("Удаление  друга с id {} у пользователя c id {}.", friendId, userId);
        findOne(getByIdQuery, userId);
        User friend = findOne(getByIdQuery, friendId);
        delete(
                deleteFriendsQuery,
                friendId,
                userId
        );
        log.info("Успешное удаление друга с id {} у пользователя c id {}.", friendId, userId);
        return friend;
    }

    @Override
    public Collection<User> getAllFriends(Long userId) {
        log.info("Получение списка друзей у пользователя с id {}", userId);
        findOne(getByIdQuery, userId);
        return findMany(getAllFriendsQuery, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        log.info("Получение списка общих друзей у пользователя с id {} и его друга c id {}", userId, friendId);
        return findMany(getCommonFriendsQuery, userId, friendId);
    }
}
