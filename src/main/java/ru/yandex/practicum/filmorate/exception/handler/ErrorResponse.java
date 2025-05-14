package ru.yandex.practicum.filmorate.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String error;
}
