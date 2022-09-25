package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired
    ItemServiceImpl itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    Item item;
    ItemDto itemDto;
    User user;
    ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(null, "name", "ya@ya.ru");
        itemRequest = new ItemRequest(null, "desc", user, LocalDateTime.now());
        item = new Item(null, "name", "desc", true, user, itemRequest);
    }

    @Test
    void addItemTest() throws ValidationException, WrongIdException {
        assertThrows(WrongIdException.class, () -> itemService.addItem(ItemMapper.toItemDto(item), 1L));
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> itemService.addItem(
                new ItemDto(null, "name", "desc", null, user, null), userId));
        assertThrows(ValidationException.class, () -> itemService.addItem(
                new ItemDto(null, null, "desc", null, user, null), userId));
        assertThrows(ValidationException.class, () -> itemService.addItem(
                new ItemDto(null, "name", null, null, user, null), userId));
        itemDto = itemService.addItem(ItemMapper.toItemDto(item), userId);
        assertNotNull(itemDto);
        assertEquals(item.getName(), itemDto.getName());
    }

    @Test
    void refreshItemTest() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        assertThrows(WrongIdException.class, () -> itemService.refreshItem(
                ItemMapper.toItemDto(item), 1L, userId));
        Long itemId = itemService.addItem(ItemMapper.toItemDto(item), userId).getId();
        assertThrows(WrongIdException.class, () -> itemService.refreshItem(
                ItemMapper.toItemDto(item), itemId, 100L));
        itemDto = itemService.refreshItem(
                new ItemDto(null,"newName", "newDesc",null, null, null),
                itemId, userId);
        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), "newName");
        assertEquals(itemDto.getDescription(), "newDesc");
    }

    @Test
    void getItemFromIdTest() throws WrongIdException, ValidationException {
        Long userId = userRepository.save(user).getId();
        assertThrows(WrongIdException.class, () -> itemService.getItemFromId(userId, 1L));
        Long itemId = itemService.addItem(ItemMapper.toItemDto(item), userId).getId();
        assertNotNull(itemService.getItemFromId(userId, itemId));
        assertEquals(itemService.getItemFromId(userId, itemId).getName(), item.getName());
    }


}
