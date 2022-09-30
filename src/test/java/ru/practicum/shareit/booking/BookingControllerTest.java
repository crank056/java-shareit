package ru.practicum.shareit.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.AvailableException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.util.Status;

import javax.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
    @Mock
    private BookingService bookingService;
    @InjectMocks
    private BookingController bookingController;
    private MockMvc mvc;
    private ItemDto itemDto;
    private UserDto userDto;
    private BookingDto bookingDto;
    private BookingItemDto bookingItemDto;
    private final ObjectMapper mapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        start = LocalDateTime.now();
        end = LocalDateTime.now().plusDays(1);
        userDto = new UserDto(1L, "name", "ya@ya.ru");
        itemDto = new ItemDto(1L, "name", "desc", true, userDto.getId(), null);
        bookingDto = new BookingDto(1L, start, end, itemDto, userDto, Status.WAITING);
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getItem().getOwnerId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())));
        ;
    }

    @Test
    void updateBookingTest() throws Exception {
        BookingDto updatedBooking = new BookingDto(1L, start, end, itemDto, userDto, Status.APPROVED);
        when(bookingService.updateBooking(anyLong(), anyLong(), any())).thenReturn(updatedBooking);
        mvc.perform(MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getItem().getOwnerId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())));
    }

    @Test
    void getBookingFromIdTest() throws Exception {
        when(bookingService.getBookingFromId(anyLong(), any())).thenReturn(bookingDto);
        mvc.perform(MockMvcRequestBuilders.get("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", notNullValue()))
                .andExpect(jsonPath("$.end", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getItem().getOwnerId()), Long.class))
                .andExpect(jsonPath("$.status", is(Status.WAITING.toString())));
    }

    @Test
    void getUserBookingsWithStatusTest() throws Exception {
        when(bookingService.getBookingsFromUserId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(
                List.of(bookingDto));
        mvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", notNullValue()))
                .andExpect(jsonPath("$.[0].end", notNullValue()))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllUserBookingsTest() throws Exception {
        when(bookingService.getBookingsFromOwnerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(
                List.of(bookingDto));
        mvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.[0].start", notNullValue()))
                .andExpect(jsonPath("$.[0].end", notNullValue()))
                .andExpect(jsonPath("$.[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.[0].status", is(bookingDto.getStatus().toString())));
    }
}

