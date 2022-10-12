package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase
@SpringBootTest
@Transactional
public class RequestServiceTest {
    @Autowired
    private RequestServiceImpl requestService;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private User user;
    private User user2;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User(null, "name", "ya@ya.ru");
        user2 = new User(null, "name2", "ya2@ya.ru");
        itemRequest = new ItemRequest(null, "desc", user, LocalDateTime.now());
        item = new Item(null, "name", "desc", true, user, null);
    }

    @Test
    void addRequestWrongUserTest() {
        Long userId = userRepository.save(user).getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto(
            null, "desc", userId, LocalDateTime.now(), List.of(ItemMapper.toItemDto(item)));
        assertThrows(WrongIdException.class, () -> requestService.addRequest(1000L, itemRequestDto));
    }

    @Test
    void addRequest() {
        Long userId = userRepository.save(user).getId();
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                null, "desc", userId, LocalDateTime.now(), List.of(ItemMapper.toItemDto(item)));
        ItemRequestDto itemRequestDtoSaved = requestService.addRequest(userId, itemRequestDto);
        assertNotNull(itemRequestDtoSaved);
        assertEquals(itemRequestDto.getRequesterId(), itemRequestDtoSaved.getRequesterId());
    }

    @Test
    void getAllRequestFromWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> requestService.getAllRequests(1000L));
    }


    @Test
    void getAllRequestTest() {
        Long userId = userRepository.save(user).getId();
        Long requestId = requestRepository.save(itemRequest).getId();
        List<ItemRequestDto> itemRequestDtos = requestService.getAllRequests(userId);
        assertNotNull(itemRequestDtos);
        assertEquals(requestId, itemRequestDtos.get(0).getId());
    }

    @Test
    void getAllWithPaginationWrongUserIdTest() {
        assertThrows(WrongIdException.class, () -> requestService.getAllWithPagination(1000L, 0, 20));
    }

    @Test
    void getAllWithPaginationTest() {
        userRepository.save(user);
        Long user2Id = userRepository.save(user2).getId();
        Long requestId = requestRepository.save(itemRequest).getId();
        List<ItemRequestDto> itemRequestDtos = requestService.getAllWithPagination(user2Id, 0, 20);
        assertNotNull(itemRequestDtos);
        assertEquals(requestId, itemRequestDtos.get(0).getId());
    }

    @Test
    void getRequestFromIdWrongUserTest() {
        userRepository.save(user);
        Long requestId = requestRepository.save(itemRequest).getId();
        assertThrows(WrongIdException.class, () -> requestService.getRequestFromId(1000L, requestId));
    }

    @Test
    void getRequestFromWrongIdTest() {
        Long userId = userRepository.save(user).getId();
        assertThrows(WrongIdException.class, () -> requestService.getRequestFromId(userId, 1000L));
    }

    @Test
    void getRequestFromIdTest() throws WrongIdException {
        Long userId = userRepository.save(user).getId();
        Long requestId = requestRepository.save(itemRequest).getId();
        ItemRequestDto itemRequestDto = requestService.getRequestFromId(userId, requestId);
        assertNotNull(itemRequestDto);
        assertEquals(requestId, itemRequestDto.getId());
    }
}
