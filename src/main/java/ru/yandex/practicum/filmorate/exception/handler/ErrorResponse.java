package ru.yandex.practicum.filmorate.exception.handler;

import lombok.*;

@Builder
@Getter @AllArgsConstructor
public class ErrorResponse {
    private final String error;
}
