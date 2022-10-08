package ru.practicum.shareit.user;

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
    void findAllTest() {
        User savedUser = userRepository.save(user);
        List<UserDto> users = userService.findAll();
        assertEquals(1, users.size());
        assertEquals(userRepository.getReferenceById(savedUser.getId()).getName(), user.getName());
        assertEquals(userRepository.getReferenceById(savedUser.getId()).getEmail(), user.getEmail());
    }

    @Test
    void findByWrongId() {
        assertThrows(WrongIdException.class, () -> userService.findById(1L));
    }

    @Test
    void testFindById() {
        UserDto savedUser = UserMapper.toUserDto(userRepository.save(user));
        assertEquals(userService.findById(savedUser.getId()), savedUser);
    }

    @Test
    void createTestWrongName() {
        assertThrows(ValidationException.class, () -> userService.create(
            new UserDto(null, null, "email@ya.ru")));
    }

    @Test
    void createNullUserTest() {
        assertThrows(ValidationException.class, () -> userService.create(null));
    }

    @Test
    void createWrongEmailTest() {
        assertThrows(ValidationException.class, () -> userService.create(new UserDto(
            null, "Name", "email")));
    }

    @Test
    void createTest() {
        UserDto savedUser = userService.create(UserMapper.toUserDto(user));
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getName(), savedUser.getName());
    }

    @Test
    void updateNullDtoTest() {
        assertThrows(ValidationException.class, () -> userService.update(1L, null));
    }

    @Test
    void updateWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> userService.update(
            1L, new UserDto(null, "update", "update@ya.ru")));
    }

    @Test
    void updateTest() {
        User savedUser = userRepository.save(user);
        userService.update(savedUser.getId(), new UserDto(null, "update", "update@ya.ru"));
        UserDto updatedUser = userService.findById(savedUser.getId());
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("update", updatedUser.getName());
        assertEquals("update@ya.ru", savedUser.getEmail());
    }

    @Test
    void deleteWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> userService.delete(1L));
    }

    @Test
    void deleteTest() {
        Long savedUserId = userRepository.save(user).getId();
        userService.delete(savedUserId);
        assertFalse(userRepository.existsById(savedUserId));
    }
}
