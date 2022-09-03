package ru.practicum.shareit.booking.service;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.AvailableException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(Long userId,BookingDto bookingDto) throws ValidationException, AvailableException;

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) throws WrongIdException, AccessException;

    BookingDto getBookingFromId(Long userId, Long bookingId) throws AccessException;

    List<BookingDto> getUserBookingsWithStatus(Long userId, String state) throws ValidationException;

    List<BookingDto> getUserBookings(Long userId, String state);
}
