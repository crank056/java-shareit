package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long bookingId,
                                                @RequestParam Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingFromId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBookingFromId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookingsWithStatus(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "state", defaultValue = "ALL")
        String stateParam,
        @PositiveOrZero
        @RequestParam(name = "from", defaultValue = "0")
        Integer from,
        @Positive @RequestParam(name = "size", defaultValue = "10")
        Integer size) {
        BookingState state = BookingState.from(stateParam)
            .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getUserBookingsWithStatus(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserBookings(
        @RequestHeader("X-Sharer-User-Id") long userId,
        @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
            .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }
}
