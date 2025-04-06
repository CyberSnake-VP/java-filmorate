package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface Controller{
  @PostMapping
    User create(@Valid @RequestBody User user);
  @PutMapping
    User update(@Valid @RequestBody User user);
  @GetMapping
    Collection<User> getAll();
}
