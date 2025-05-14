package ru.yandex.practicum.filmorate.storage.indb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BaseDbStorage<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    // Общий метод для получения одного объекта
    protected T findOne(String query, Object... params) {
        try {
            return jdbc.queryForObject(query, mapper, params);
        } catch (DataAccessException e) {
            throw new NotFoundException("Ошибка при получении данных, проверьте корректность введенных полей.");
        }
    }

    // Общий метод для получения списка объектов
    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    // Общий метод для обновления
    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            log.error("Ошибка при обновлении объекта");
            throw new NotFoundException("Не удалось обновить данные");
        }
    }

    // Общий метод для удаления объекта
    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    // Общий метод для записи объекта
    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(con -> {
                PreparedStatement ps = con
                        .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int idx = 0; idx < params.length; idx++) {
                    ps.setObject(idx + 1, params[idx]);
                }
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            log.error("Ошибка при записи объекта");
            throw new NotFoundException("Некорректное заполнение полей.");
        }

        Long id = keyHolder.getKeyAs(Long.class);

        // Возвращаем id нового пользователя
        if (id != null) {
            return id;
        } else {
            log.error("Ошибка при генерации id объекта при создании.");
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

}
