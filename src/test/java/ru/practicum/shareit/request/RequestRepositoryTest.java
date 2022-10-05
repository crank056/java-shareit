package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    RequestRepository requestRepository;
    private User user;
    private User user2;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private Pageable page;
    private List<ItemRequest> list;

    @BeforeEach
    void setUp() {
        page = PageRequest.of(0, 20, Sort.by("id").ascending());
        user = new User(null, "owner", "ya@ya.ru");
        user2 = new User(null, "name", "ya2@ya.ru");
        itemRequest = new ItemRequest(null, "desc", user, LocalDateTime.now());
        itemRequest2 = new ItemRequest(null, "desc", user2, LocalDateTime.now());
        item = new Item(null, "name", "description", true, user, itemRequest);
        testEntityManager.persist(user);
        testEntityManager.persist(itemRequest);
        testEntityManager.persist(user2);
        testEntityManager.persist(itemRequest2);
        testEntityManager.persist(item);
    }

    @Test
    void findAllByRequester() {
        list = requestRepository.findAllByRequesterOrderByCreatedDesc(user);
        assertEquals(1, list.size());
        assertEquals(itemRequest, list.get(0));
    }

    @Test
    void findAllByRequesterWithPage() {
        list = requestRepository.findAllByRequesterNotOrderByCreatedDesc(page, user).getContent();
        assertEquals(1, list.size());
        assertEquals(itemRequest2, list.get(0));
    }
}
