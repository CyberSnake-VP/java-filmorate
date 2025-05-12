package ru.yandex.practicum.filmorate.exception.handler;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLNonTransientException;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.warn(e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleInternalServerError(final InternalServerException e) {
        log.warn(e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleJdbcException(final SQLException e) {
        log.warn(e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }


    // ValidateException является супер классом ConstraintViolationException, будут обрабатываться ошибки валидации
    // переменных пути запроса, а так же общая валидация в основной логике приложения.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleValidate(final ValidationException e) {
        log.warn(e.getMessage());
        return ErrorResponse.builder().error(e.getMessage()).build();
    }

    // Обрабатываем ошибку валидации полей объекта.
    // Получаем дефолтное сообщение при ошибке валидации у полей в классах User, Film
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleArgumentNotValid(final MethodArgumentNotValidException e) {
        log.warn(Objects.requireNonNull(e.getFieldError()).getDefaultMessage());
        return ErrorResponse.builder().error(e.getFieldError().getDefaultMessage()).build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleError(final RuntimeException e) {
        log.warn(e.getMessage());
        return ErrorResponse.builder().error("Неизвестная ошибка.").build();
    }
}
