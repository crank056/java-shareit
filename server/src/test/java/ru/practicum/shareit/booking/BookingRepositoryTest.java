package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BookingRepository bookingRepository;
    private User owner;
    private User booker;
    private Item itemFuture;
    private Item itemPast;
    private Item itemCurrent;
    private Booking bookingFuture;
    private Booking bookingCurrent;
    private Booking bookingPast;
    private Pageable page;
    private List<Booking> list;

    @BeforeEach
    void setUp() {
        page = PageRequest.of(0, 20, Sort.by("id").ascending());
        owner = new User(null, "owner", "ya@ya.ru");
        booker = new User(null, "booker", "ya1@ya.ru");
        itemFuture = new Item(null, "name", "description", true, owner, null);
        itemPast = new Item(null, "name", "description", true, owner, null);
        itemCurrent = new Item(null, "name", "description", true, owner, null);
        bookingFuture = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                itemFuture, booker, Status.WAITING);
        bookingCurrent = new Booking(null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1),
                itemCurrent, booker, Status.WAITING);
        bookingPast = new Booking(null, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                itemPast, booker, Status.WAITING);
        testEntityManager.persist(owner);
        testEntityManager.persist(booker);
        testEntityManager.persist(itemFuture);
        testEntityManager.persist(itemPast);
        testEntityManager.persist(itemCurrent);
        testEntityManager.persist(bookingFuture);
        testEntityManager.persist(bookingCurrent);
        testEntityManager.persist(bookingPast);
    }

    @Test
    void findAllByBookerIdTest() {
        list = bookingRepository.findAllByBookerId(booker.getId(), page);
        assertEquals(3, list.size());
        assertEquals(itemFuture.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdInCurrentTest() {
        list = bookingRepository.findAllByBookerIdInCurrent(
                booker.getId(), LocalDateTime.now(), LocalDateTime.now(), page);
        assertEquals(1, list.size());
        assertEquals(itemCurrent.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerAndEndBeforeOrderByStartDescTest() {
        list = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(booker, LocalDateTime.now());
        assertEquals(1, list.size());
        assertEquals(itemPast.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdInPastWithPage() {
        list = bookingRepository.findAllByBookerIdInPastWithPage(booker.getId(), LocalDateTime.now(), page);
        assertEquals(1, list.size());
        assertEquals(itemPast.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdInFutureTest() {
        list = bookingRepository.findAllByBookerIdInFuture(booker.getId(),
                LocalDateTime.now(), LocalDateTime.now(), page);
        assertEquals(1, list.size());
        assertEquals(itemFuture.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByBookerIdAndStatusWaitingTest() {
        list = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.WAITING, page);
        assertEquals(3, list.size());
        assertEquals(itemFuture.getId(), list.get(0).getItem().getId());
        assertEquals(itemCurrent.getId(), list.get(1).getItem().getId());
        assertEquals(itemPast.getId(), list.get(2).getItem().getId());
    }

    @Test
    void findAllByBookerIdAndStatusApprovedTest() {
        list = bookingRepository.findAllByBookerIdAndStatus(booker.getId(), Status.APPROVED, page);
        assertEquals(0, list.size());
    }

    @Test
    void findAllByItemOwnerInCurrentTest() {
        list = bookingRepository.findAllByItemOwnerInCurrent(owner, LocalDateTime.now(), LocalDateTime.now(), page);
        assertEquals(1, list.size());
        assertEquals(itemCurrent.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByItemOwnerInPastWithPageTest() {
        list = bookingRepository.findAllByItemOwnerInPastWithPage(owner, LocalDateTime.now(), page);
        assertEquals(1, list.size());
        assertEquals(itemPast.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByItemOwnerInFutureTest() {
        list = bookingRepository.findAllByItemOwnerInFuture(owner, LocalDateTime.now(), LocalDateTime.now(), page);
        assertEquals(1, list.size());
        assertEquals(itemFuture.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByItemOwnerAndStatusWaitingTest() {
        list = bookingRepository.findAllByItemOwnerAndStatus(owner, Status.WAITING, page);
        assertEquals(3, list.size());
        assertEquals(itemFuture.getId(), list.get(0).getItem().getId());
        assertEquals(itemCurrent.getId(), list.get(1).getItem().getId());
        assertEquals(itemPast.getId(), list.get(2).getItem().getId());
    }

    @Test
    void findAllByItemOwnerAndStatusApprovedTest() {
        list = bookingRepository.findAllByItemOwnerAndStatus(owner, Status.APPROVED, page);
        assertEquals(0, list.size());
    }

    @Test
    void findAllByItemTest() {
        list = bookingRepository.findAllByItem(itemFuture);
        assertEquals(1, list.size());
        assertEquals(itemFuture.getId(), list.get(0).getItem().getId());
    }

    @Test
    void findAllByOwnerTest() {
        list = bookingRepository.findAllByOwner(owner, page);
        assertEquals(3, list.size());
        assertEquals(itemFuture.getId(), list.get(0).getItem().getId());
        assertEquals(itemCurrent.getId(), list.get(1).getItem().getId());
        assertEquals(itemPast.getId(), list.get(2).getItem().getId());
    }
}
