package ru.yandex.practicum.filmorate.validate;

public interface Validate {
    void isExist(boolean existing, Long id);

    void isNull(Long id);
}
