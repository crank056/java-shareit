package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll();
    }

    @PatchMapping("/{id}")
    public UserDto refreshUser(@RequestBody UserDto userDto, @PathVariable Long id)
            throws ValidationException, WrongIdException {
        log.info("Запрос PUT /users получен, объект: {}", userDto);
        return userService.update(id, userDto);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) throws ValidationException {
        log.info("Запрос POST /users получен, объект: {}", userDto);
        return userService.create(userDto);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFromId(@PathVariable Long id) throws WrongIdException {
        log.info("Запрос DELETE /users получен, объект: {}", id);
        return userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getUserFromId(@PathVariable long id) throws WrongIdException {
        log.info("Запрос GET /users/{id} получен: {}", id);
        return userService.findById(id);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Map<String, String> handleWrongIdException(final WrongIdException e) {
        return Map.of("Объект с таким Id не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("Пользователь не прошел валидацию", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private Map<String, String> handleRuntimeException(final RuntimeException e) {
        return Map.of("Возникло исключение", e.getMessage());
    }
}

