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
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;
import ru.practicum.shareit.util.BookingState;
import ru.practicum.shareit.util.Status;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
public class BookingServiceTest {
    @Autowired
    private BookingServiceImpl bookingService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private Item item;
    private User user;
    private User user2;
    private Booking booking;
    private Booking booking2;

    @BeforeEach
    void setUp() {
        user = new User(null, "name", "ya@ya.ru");
        user2 = new User(null, "name2", "ya2@ya.ru");
        item = new Item(null, "name", "desc", true, user, null);
        booking = new Booking(
                null, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item,
                user, Status.APPROVED);
        booking2 = new Booking(
                null, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item,
                user2, Status.APPROVED);
    }

    @Test
    void createBookingNullValidExceptionTest() {
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L, null));
    }

    @Test
    void createBookingInPastTest() {
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
            new BookingItemDto(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                1L, 1L, Status.WAITING)));
    }

    @Test
    void createBookingEndInPastTest() {
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
            new BookingItemDto(null, LocalDateTime.now(), LocalDateTime.now().minusDays(1),
                1L, 1L, Status.WAITING)));
    }

    @Test
    void createBookingEndBeforeStartTest() {
        assertThrows(ValidationException.class, () -> bookingService.createBooking(1L,
            new BookingItemDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now(),
                1L, 1L, Status.WAITING)));
    }

    @Test
    void createBookingWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> bookingService.createBooking(1000L,
            new BookingItemDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                1L, 1L, Status.WAITING)));
    }

    @Test
    void createBookingItemNotAvailableTest() {
        item.setIsAvailable(false);
        Long userId = userRepository.save(user).getId();
        Long itemId = itemRepository.save(item).getId();
        assertThrows(AvailableException.class, () -> bookingService.createBooking(userId,
            new BookingItemDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                itemId, userId, Status.WAITING)));
    }

    @Test
    void createBookingFromOwnerTest() {
        Long userId = userRepository.save(user).getId();
        Long savedItemId = itemRepository.save(item).getId();
        assertThrows(AccessException.class, () -> bookingService.createBooking(userId,
            new BookingItemDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                savedItemId, userId, Status.WAITING)));
    }

    @Test
    void createBookingTest()
        throws ValidationException, AccessException, WrongIdException, NotFoundException, AvailableException {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        userRepository.save(user);
        itemRepository.save(item);
        Long user2Id = userRepository.save(user2).getId();
        BookingDto bookingDto = bookingService.createBooking(user2Id, BookingMapper.toBookingItemDto(booking));
        assertNotNull(bookingDto);
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
    }

    @Test
    void updateBookingWrongBookingIdTest(){
        assertThrows(WrongIdException.class, () -> bookingService.updateBooking(
            1L, 100L, true));
    }

    @Test
    void updateBookingWrongUserIdTest() {
        userRepository.save(user);
        itemRepository.save(item);
        Long bookingId = bookingRepository.save(booking).getId();
        assertThrows(WrongIdException.class, () -> bookingService.updateBooking(
            100L, bookingId, true));
    }

    @Test
    void updateBookingNotOwnerTest() {
        userRepository.save(user);
        itemRepository.save(item);
        Long bookingId = bookingRepository.save(booking).getId();
        Long user2Id = userRepository.save(user2).getId();
        assertThrows(AccessException.class, () -> bookingService.updateBooking(
            user2Id, bookingId, true));
    }

    @Test
    void updateBookingApprovedTest() {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        booking.setStatus(Status.APPROVED);
        Long bookingId = bookingRepository.save(booking).getId();
        assertThrows(ValidationException.class, () -> bookingService.updateBooking(
            userId, bookingId, true));
    }

    @Test
    void updateBookingTest() throws AccessException, ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        booking.setStatus(Status.WAITING);
        Long bookingId = bookingRepository.save(booking).getId();
        BookingDto bookingDto = bookingService.updateBooking(userId, bookingId, true);
        assertNotNull(bookingDto);
        assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }

    @Test
    void getBookingFromWrongIdTest() {
        assertThrows(WrongIdException.class, () -> bookingService.getBookingFromId(
            1L, 1L));
    }

    @Test
    void getBookingFromIdNotFoundTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(NotFoundException.class, () -> bookingService.getBookingFromId(
            userId, 1L));
    }

    @Test
    void getBookingFromIdNotOwnerTest() {
        userRepository.save(user);
        itemRepository.save(item);
        Long bookingId = bookingRepository.save(booking).getId();
        Long user2Id = userRepository.save(user2).getId();
        assertThrows(AccessException.class, () -> bookingService.getBookingFromId(
            user2Id, bookingId));
    }

    @Test
    void getBookingFromId() throws AccessException, WrongIdException, NotFoundException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        Long bookingId = bookingRepository.save(booking).getId();
        userRepository.save(user2);
        BookingDto bookingDto = bookingService.getBookingFromId(userId, bookingId);
        assertNotNull(bookingDto);
        assertEquals(BookingMapper.toBookingDto(booking), bookingDto);
    }

    @Test
    void getFromWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> bookingService.getBookingsFromUserId(
            1L, "ALL", 0, 20));
    }

    @Test
    void getFromUserIdWrongSizeTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> bookingService.getBookingsFromUserId(
            userId, BookingState.ALL.toString(), -1, 0));
    }

    @Test
    void getFromUserIdWrongStatusTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> bookingService.getBookingsFromUserId(
            userId, BookingState.UNSUPPORTED_STATUS.toString(), 0, 20));
    }

    @Test
    void getFromUserIdStatusAll() throws ValidationException, WrongIdException {
        userRepository.save(user);
        itemRepository.save(item);
        Long user2Id = userRepository.save(user2).getId();
        Long bookingId = bookingRepository.save(booking2).getId();
        List<BookingDto> list = bookingService.getBookingsFromUserId(user2Id, BookingState.ALL.toString(), 0, 20);
        assertEquals(1, list.size());
        assertEquals(BookingMapper.toBookingDto(bookingRepository.getReferenceById(bookingId)), list.get(0));
    }

    @Test
    void getFromUserIdStatusCurrent() throws ValidationException, WrongIdException {
        userRepository.save(user);
        itemRepository.save(item);
        Long user2Id = userRepository.save(user2).getId();
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromUserId(user2Id, BookingState.CURRENT.toString(), 0, 20);
        assertEquals(0, list.size());
    }

    @Test
    void getFromUserIdStatusPast() throws ValidationException, WrongIdException {
        userRepository.save(user);
        itemRepository.save(item);
        Long user2Id = userRepository.save(user2).getId();
        Long bookingId = bookingRepository.save(booking2).getId();
        List<BookingDto> list = bookingService.getBookingsFromUserId(user2Id, BookingState.PAST.toString(), 0, 20);
        assertEquals(1, list.size());
        assertEquals(BookingMapper.toBookingDto(bookingRepository.getReferenceById(bookingId)), list.get(0));
    }

    @Test
    void getFromUserIdStatusFuture() throws ValidationException, WrongIdException {
        userRepository.save(user);
        itemRepository.save(item);
        Long user2Id = userRepository.save(user2).getId();
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromUserId(user2Id, BookingState.FUTURE.toString(), 0, 20);
        assertEquals(0, list.size());
    }

    @Test
    void getFromUserIdStatusWaiting() throws ValidationException, WrongIdException {
        userRepository.save(user);
        itemRepository.save(item);
        Long user2Id = userRepository.save(user2).getId();
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromUserId(user2Id, BookingState.WAITING.toString(), 0, 20);
        assertEquals(0, list.size());
    }

    @Test
    void getFromUserIdStatusRejected() throws ValidationException, WrongIdException {
        userRepository.save(user);
        itemRepository.save(item);
        Long user2Id = userRepository.save(user2).getId();
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromUserId(user2Id, BookingState.REJECTED.toString(), 0, 20);
        assertEquals(0, list.size());
    }

    @Test
    void getFromWrongOwnerIdTest() {
        assertThrows(WrongIdException.class, () -> bookingService.getBookingsFromOwnerId(
            1L, "ALL", 0, 20));
    }

    @Test
    void getFromOwnerIdWrongSizeTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> bookingService.getBookingsFromOwnerId(
            userId, BookingState.ALL.toString(), -1, 0));
    }

    @Test
    void getFromOwnerIdWrongStatusTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(ValidationException.class, () -> bookingService.getBookingsFromOwnerId(
            userId, BookingState.UNSUPPORTED_STATUS.toString(), 0, 20));
    }

    @Test
    void getFromOwnerIdStatusAll() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        userRepository.save(user2);
        Long bookingId = bookingRepository.save(booking2).getId();
        List<BookingDto> list = bookingService.getBookingsFromOwnerId(userId, BookingState.ALL.toString(), 0, 20);
        assertEquals(1, list.size());
        assertEquals(BookingMapper.toBookingDto(bookingRepository.getReferenceById(bookingId)), list.get(0));
    }

    @Test
    void getFromOwnerIdStatusCurrent() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromOwnerId(userId, BookingState.CURRENT.toString(), 0, 20);
        assertEquals(0, list.size());
    }

    @Test
    void getFromOwnerIdStatusPast() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        userRepository.save(user2);
        Long bookingId = bookingRepository.save(booking2).getId();
        List<BookingDto> list = bookingService.getBookingsFromOwnerId(userId, BookingState.PAST.toString(), 0, 20);
        assertEquals(1, list.size());
        assertEquals(BookingMapper.toBookingDto(bookingRepository.getReferenceById(bookingId)), list.get(0));
    }

    @Test
    void getFromOwnerIdStatusFuture() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromOwnerId(userId, BookingState.FUTURE.toString(), 0, 20);
        assertEquals(0, list.size());
    }

    @Test
    void getFromOwnerIdStatusWaiting() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromOwnerId(userId, BookingState.WAITING.toString(), 0, 20);
        assertEquals(0, list.size());
    }

    @Test
    void getFromOwnerIdStatusRejected() throws ValidationException, WrongIdException {
        Long userId = userRepository.save(user).getId();
        itemRepository.save(item);
        userRepository.save(user2);
        bookingRepository.save(booking2);
        List<BookingDto> list = bookingService.getBookingsFromOwnerId(userId, BookingState.REJECTED.toString(), 0, 20);
        assertEquals(0, list.size());
    }
}
