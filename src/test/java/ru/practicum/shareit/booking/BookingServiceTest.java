package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;
import ru.practicum.shareit.util.Status;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
public class BookingServiceTest {
    @Autowired
    BookingServiceImpl bookingService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    Item item;
    Item item2;
    User user;
    User user2;
    ItemRequest itemRequest;
    Comment comment;
    Booking booking;

    @BeforeEach
    void setUp() {
        user = new User(null, "name", "ya@ya.ru");
        user2 = new User(null, "name2", "ya2@ya.ru");
        itemRequest = new ItemRequest(null, "desc", user, LocalDateTime.now());
        item = new Item(null, "name", "desc", true, user, null);
        item2 = new Item(null, "name", "desc", true, user2, null);
        booking = new Booking(
                null, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item,
                user, Status.APPROVED);
        comment = new Comment(null, "text", item, user, LocalDateTime.now());
    }

    @Test
    void createBookingTest()
            throws ValidationException, AccessException, WrongIdException, NotFoundException, AvailableException {
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, null));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
                new BookingItemDto(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                        1L, 1L, Status.WAITING)));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
                new BookingItemDto(null, LocalDateTime.now(), LocalDateTime.now().minusDays(1),
                        1L, 1L, Status.WAITING)));
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
                new BookingItemDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now(),
                        1L, 1L, Status.WAITING)));
        assertThrows(WrongIdException.class, () -> bookingService.createBooking(1000L,
                new BookingItemDto(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        1L, 1L, Status.WAITING)));
        item.setIsAvailable(false);
        userRepository.save(user);
        itemRepository.save(item);
        assertThrows(AvailableException.class, () -> bookingService.createBooking(1L,
                new BookingItemDto(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        1L, 1L, Status.WAITING)));
        item.setIsAvailable(true);
        itemRepository.save(item);
        assertThrows(AccessException.class, () -> bookingService.createBooking(1L,
                new BookingItemDto(null, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                        1L, 1L, Status.WAITING)));
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        userRepository.save(user2);
        BookingDto bookingDto = bookingService.createBooking(2L, BookingMapper.toBookingItemDto(booking));
        assertNotNull(bookingDto);
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
    }

    @Test
    void updateBookingTest() throws AccessException, ValidationException, WrongIdException {
        assertThrows(WrongIdException.class, () -> bookingService.updateBooking(
                1L, 100L, true));
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        booking.setStatus(Status.APPROVED);
        Long bookingId = bookingRepository.save(booking).getId();
        Long user2Id = userRepository.save(user2).getId();
        assertThrows(WrongIdException.class, () -> bookingService.updateBooking(
                100L, bookingId, true));
        assertThrows(AccessException.class, () -> bookingService.updateBooking(
                user2Id, bookingId, true));
        assertThrows(ValidationException.class, () -> bookingService.updateBooking(
                userId, bookingId, true));
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        BookingDto bookingDto = bookingService.updateBooking(userId, bookingId, true);
        assertNotNull(bookingDto);
        assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }

    @Test
    void getBookingFromId() throws AccessException, WrongIdException, NotFoundException {
        assertThrows(WrongIdException.class, () -> bookingService.getBookingFromId(
                1L, 1L));
        Long userId = userRepository.save(user).getId();
        assertThrows(NotFoundException.class, () -> bookingService.getBookingFromId(
                userId, 1L));
        itemRepository.save(item);
        Long bookingId = bookingRepository.save(booking).getId();
        Long user2Id = userRepository.save(user2).getId();
        assertThrows(AccessException.class, () -> bookingService.getBookingFromId(
                user2Id, bookingId));
        BookingDto bookingDto = bookingService.getBookingFromId(userId, bookingId);
        assertNotNull(bookingDto);
        assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }





}
