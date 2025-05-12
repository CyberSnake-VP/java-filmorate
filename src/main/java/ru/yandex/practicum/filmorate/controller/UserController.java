package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated

public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(@Qualifier("UserDbService") UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAll() {
        return userService.getAll();
    }

    @GetMapping("{id}")
    public User getById(@PathVariable(value = "id", required = false) @Min(1) Long id) {
        return userService.getById(id);
    }

    @GetMapping("{id}/friends")
    public Collection<User> getAllFriends(@PathVariable("id") @Min(1) Long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable("id") @Min(1) Long id,
                                             @PathVariable("otherId") @Min(1) Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") @Min(1) Long userId,
                          @PathVariable("friendId") @Min(1) Long friendId) {
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable("userId") @Min(1) Long userId,
                             @PathVariable("friendId") @Min(1) Long friendId) {
        return userService.deleteFriend(userId, friendId);
    }

}
