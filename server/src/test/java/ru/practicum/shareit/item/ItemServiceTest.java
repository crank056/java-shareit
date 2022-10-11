package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;
import ru.practicum.shareit.util.Status;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
public class ItemServiceTest {
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private Item item;
    private Item item2;
    private ItemDto itemDto;
    private User user;
    private Comment comment;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(null, "name", "ya@ya.ru");
        item = new Item(null, "name", "desc", true, user, null);
        item2 = new Item(null, "name", "desc", true, user, null);
        booking = new Booking(
                null, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item,
                user, Status.APPROVED);
        comment = new Comment(null, "text", item, user, LocalDateTime.now());
    }

    @Test
    void addItemWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> itemService.addItem(ItemMapper.toItemDto(item), 1L));
    }

    @Test
    void addItemNotAvailableTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> itemService.addItem(
            new ItemDto(null, "name", "desc", null, userId, null), userId));
    }

    @Test
    void addItemNullNameTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> itemService.addItem(
            new ItemDto(null, null, "desc", true, userId, null), userId));
    }

    @Test
    void addItemNullDescTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> itemService.addItem(
            new ItemDto(null, "name", null, true, userId, null), userId));
    }

    @Test
    void addItemTest() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemDto = itemService.addItem(ItemMapper.toItemDto(item), userId);
        assertNotNull(itemDto);
        assertEquals(item.getName(), itemDto.getName());
    }

    @Test
    void refreshItemWrongItemIdTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(WrongIdException.class, () -> itemService.refreshItem(
            ItemMapper.toItemDto(item), 1L, userId));
    }

    @Test
    void refreshItemWrongUserIdTest() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemService.addItem(ItemMapper.toItemDto(item), userId).getId();
        assertThrows(WrongIdException.class, () -> itemService.refreshItem(
            ItemMapper.toItemDto(item), itemId, 100L));
    }

    @Test
    void refreshItemTest() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemService.addItem(ItemMapper.toItemDto(item), userId).getId();
        itemDto = itemService.refreshItem(
                new ItemDto(null, "newName", "newDesc", null, null, null),
                itemId, userId);
        assertNotNull(itemDto);
        assertEquals(itemDto.getName(), "newName");
        assertEquals(itemDto.getDescription(), "newDesc");
    }

    @Test
    void getItemFromWrongItemIdTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(WrongIdException.class, () -> itemService.getItemFromId(userId, 1L));
    }

    @Test
    void getItemFromItemIdTest() throws WrongIdException, ValidationException {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemService.addItem(ItemMapper.toItemDto(item), userId).getId();
        ItemBookingDto itemBookingDto = itemService.getItemFromId(userId, itemId);
        assertNotNull(itemBookingDto);
        assertEquals(itemBookingDto.getName(), item.getName());
    }

    @Test
    void getItemsFromWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> itemService.getAllItemsFromUserId(1L, 1, 20));
    }

    @Test
    void getItemsFromUserIdWrongSizeTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class,
            () -> itemService.getAllItemsFromUserId(userId, -1, 0));
    }

    @Test
    void getItemFromUserIdTest() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemService.addItem(ItemMapper.toItemDto(item), userId);
        itemService.addItem(ItemMapper.toItemDto(item2), userId);
        List<ItemBookingDto> list = itemService.getAllItemsFromUserId(userId, 0, 20);
        assertEquals(2, list.size());
        assertEquals(item.getName(), list.get(0).getName());
    }

    @Test
    void getItemFromKeyWordWrongSizeTest() {
        userRepository.save(user);
        assertThrows(ValidationException.class,
            () -> itemService.getItemsFromKeyWord("decs", -1, 0));
    }

    @Test
    void getItemsFromKeWordTest() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemService.addItem(ItemMapper.toItemDto(item), userId);
        List<ItemDto> list = itemService.getItemsFromKeyWord("desc", 0, 20);
        assertEquals(1, list.size());
        assertEquals(item.getName(), list.get(0).getName());
    }

    @Test
    void addCommentEmptyTest() {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemRepository.save(item).getId();
        bookingRepository.save(booking);
        User user2 = userRepository.save(new User(null, "name", "yaya@ya.ru"));
        assertThrows(ValidationException.class, () -> itemService.addComment(
            new Comment(null, "", item, user, LocalDateTime.now()), itemId, userId));
    }

    @Test
    void addCommentNotOwnerTest() {
        userRepository.save(user);
        Long itemId = itemRepository.save(item).getId();
        bookingRepository.save(booking);
        User user2 = userRepository.save(new User(null, "name", "yaya@ya.ru"));
        assertThrows(AccessException.class, () -> itemService.addComment(
            new Comment(null, "text", item, user2, LocalDateTime.now()), itemId, user2.getId()));
    }

    @Test
    void addCommentTest() throws ValidationException, AccessException {
        Long userId = userRepository.save(user).getId();
        Long itemId = itemRepository.save(item).getId();
        bookingRepository.save(booking);
        userRepository.save(new User(null, "name", "yaya@ya.ru"));
        assertEquals(comment.getText(), itemService.addComment(comment, itemId, userId).getText());
    }
}
