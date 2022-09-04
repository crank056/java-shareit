package ru.practicum.shareit.booking.service;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.exceptions.*;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId, BookingItemDto bookingItemDto) throws ValidationException, AvailableException;

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) throws WrongIdException, AccessException, ValidationException;

    BookingDto getBookingFromId(Long userId, Long bookingId) throws AccessException, NotFoundException;

    List<BookingDto> getBookingsFromUserId(Long userId, String state) throws ValidationException, WrongIdException;

    List<BookingDto> getBookingsFromOwnerId(Long ownerId, String state) throws WrongIdException, ValidationException;
}
