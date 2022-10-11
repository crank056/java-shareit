package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingItemDtoTest {
    @Autowired
    private JacksonTester<BookingItemDto> json;

    @Test
    void testBookingDto() throws Exception {
        BookingItemDto bookingDto = new BookingItemDto(
                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                null, null, Status.APPROVED);
        JsonContent<BookingItemDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.APPROVED.toString());
    }
}

