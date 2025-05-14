package ru.yandex.practicum.filmorate.validate;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class UserValidate implements Validate {

    @Override
    public void isExist(boolean existing, Long id) {
        if (!existing) {
            String message = String.format("Пользователь с id %d, не найден.", id);
            throw new NotFoundException(message);
        }
    }

    @Override
    public void isNull(Long id) {
        if (Objects.isNull(id)) {
            String message = "id должен быть указан.";
            throw new ValidationException(message);
        }
    }

    // Проверка пользователя на уникальность email адреса, для избежания повторного добавления.
    public void isRepeat(Map<Long, User> users, User user) {
        String email = user.getEmail();
        boolean isExist = users.values().stream()
                .map(User::getEmail)
                .anyMatch(email::equals);

        if (isExist) {
            String errorMessage = String.format("Пользователь с email %s адресом уже существует.", email);
            throw new ValidationException(errorMessage);
        }
    }

}
