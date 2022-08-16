package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.NullEmailException;
import ru.practicum.shareit.exceptions.WrongEmailException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.userStorage.UserStorage;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserStorage userStorage;

    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @PatchMapping("/{id}")
    public User refreshUser(@RequestBody User user, @PathVariable Long id) {
        log.info("Запрос PUT /users получен, объект: {}", user);
        return userStorage.userRefresh(id, user);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Запрос POST /users получен, объект: {}", user);
        return userStorage.userAdd(user);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFromId(@PathVariable Long id) throws WrongIdException {
        log.info("Запрос DELETE /users получен, объект: {}", id);
        return userStorage.userDelete(id);
    }

    @GetMapping("/{id}")
    public User getUserFromId(@PathVariable long id) throws WrongIdException {
        log.info("Запрос GET /users/{id} получен: {}", id);
        return userStorage.getUserFromId(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleWrongIdException(final WrongIdException e) {
        return Map.of("Объект с таким Id не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleWrongEmailException(final WrongEmailException e) {
        return Map.of("Неверный формат email", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNullEmailException(final NullEmailException e) {
        return Map.of("Неверный email", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRuntimeException(final RuntimeException e) {
        return Map.of("Возникло исключение", e.getMessage());
    }
}

