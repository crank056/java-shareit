package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.user.userStorage.UserRepository;

import javax.transaction.Transactional;

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


}
