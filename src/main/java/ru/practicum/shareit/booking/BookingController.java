package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.model.ErrorResponse;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingItemDto bookingItemDto) throws ValidationException, AvailableException {
        log.info("Запрос POST /bookings получен, объект: {}", bookingItemDto);
        return bookingService.createBooking(userId, bookingItemDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam("approved") Boolean approved) throws AccessException, WrongIdException, ValidationException {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping(value = "/{bookingId}")
    public BookingDto getBookingFromId(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) throws AccessException, NotFoundException {
        return bookingService.getBookingFromId(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getUserBookingsWithStatus(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state)
            throws ValidationException, WrongIdException {
        return bookingService.getBookingsFromUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllUserBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state)
            throws ValidationException, WrongIdException {
        return bookingService.getBookingsFromOwnerId(userId, state);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAvailableException(final AvailableException e) {
        return Map.of("Вещь недоступна", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAvailableException(final WrongIdException e) {
        return Map.of("Вещь недоступна", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAvailableException(final NotFoundException e) {
        return Map.of("Вещь не найдена", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAvailableException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleAvailableException(final AccessException e) {
        return Map.of("Ошибка доступа", e.getMessage());
    }








}
