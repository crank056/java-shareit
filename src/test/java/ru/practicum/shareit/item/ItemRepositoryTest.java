package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository itemRepository;
    private User user;
    private Item item;
    private Pageable page;
    private List<Item> list;

    @BeforeEach
    void beforeEach() {
        page = PageRequest.of(0, 20, Sort.by("id").ascending());
        user = new User(null, "name", "ya@ya.ru");
        item = new Item(null, "name", "description", true, user, null);
        testEntityManager.persist(user);
        testEntityManager.persist(item);
    }

    @Test
    void findAllByOwnerTest() {
        list = itemRepository.findAllByOwnerOrderByIdAsc(user, page);
        assertEquals(item.getName(), list.get(0).getName());
    }

    @Test
    void findAllByRequestTest() {
        list = itemRepository.findAllByRequestId(2L);
        assertEquals(0, list.size());
    }

    @Test
    void findFromKeyWordTest() {
        list = itemRepository.findFromKeyWord("description", page);
        assertEquals(1, list.size());
        assertEquals(item.getName(), list.get(0).getName());
    }
}
