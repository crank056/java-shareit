package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoValid;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDtoValid userDtoValid) {
        return userClient.createUser(userDtoValid);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> refreshUser(@RequestBody @Valid UserDto userDto,
                                              @PathVariable long id) {
        return userClient.refreshUser(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteFromId(@PathVariable long id) {
        userClient.deleteFromId(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserFromId(@PathVariable long id) {
        return userClient.getUserFromId(id);
    }
}
