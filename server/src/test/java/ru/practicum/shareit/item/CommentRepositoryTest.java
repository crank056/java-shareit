package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.Repository.CommentRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private CommentRepository commentRepository;
    private Item item;
    private User user;
    private Comment comment;
    private List<Comment> list;

    @BeforeEach
    void beforeEach() {
        user = new User(null, "name", "ya@ya.ru");
        item = new Item(null, "name", "description", true, user, null);
        comment = new Comment(null, "text", item, user, LocalDateTime.now());
        testEntityManager.persist(user);
        testEntityManager.persist(item);
        testEntityManager.persist(comment);
    }

    @Test
    void findAllByItemTest() {
        list = commentRepository.findAllByItemOrderByCreatedAsc(item);
        assertEquals(1, list.size());
        assertEquals(comment.getText(), list.get(0).getText());
    }
}
