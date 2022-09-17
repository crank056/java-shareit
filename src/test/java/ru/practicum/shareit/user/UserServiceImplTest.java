package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.userStorage.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
public class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(null, "Name", "email@ya.ru");
    }

    @Test
    void testFindAll() {
        List<UserDto> users = userService.findAll();
        assertEquals(0, users.size());
        User savedUser = userRepository.save(user);
        users = userService.findAll();
        assertEquals(1, users.size());
        assertEquals(userRepository.getReferenceById(savedUser.getId()).getName(), user.getName());
        assertEquals(userRepository.getReferenceById(savedUser.getId()).getEmail(), user.getEmail());
    }

    @Test
    void testFindById() {
        assertThrows(WrongIdException.class, () -> userService.findById(1L));
        UserDto savedUser = UserMapper.toUserDto(userRepository.save(user));
        assertEquals(userService.findById(savedUser.getId()), savedUser);
    }

    @Test
    void createTest() {
        assertThrows(ValidationException.class, () -> userService.create(
                new UserDto(null, null, "email@ya.ru")));
        assertThrows(ValidationException.class, () -> userService.create(null));
        assertThrows(ValidationException.class, () -> userService.create( new UserDto(
                null, "Name", "email")));
        UserDto savedUser = userService.create(UserMapper.toUserDto(user));
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getName(), savedUser.getName());
    }

    @Test
    void updateTest() {
        assertThrows(WrongIdException.class, () -> userService.update(1L, null));
        User savedUser = userRepository.save(user);
        assertThrows(ValidationException.class, () -> userService.update(savedUser.getId(), null));
        userService.update(savedUser.getId(), new UserDto(null, "update", "update@ya.ru"));
        UserDto updatedUser = userService.findById(savedUser.getId());
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("update", updatedUser.getName());
        assertEquals("update@ya.ru", savedUser.getEmail());
    }

    @Test
    void deleteTest() {
        assertThrows(WrongIdException.class, () -> userService.delete(1L));
        Long savedUserId = userRepository.save(user).getId();
        assertNotNull(userRepository.getReferenceById(savedUserId));
        userService.delete(savedUserId);
        assertThrows(WrongIdException.class, () -> userService.delete(savedUserId));
    }
}
