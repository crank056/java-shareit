package ru.practicum.shareit.booking.service;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.itemRepository.ItemRepository;
import ru.practicum.shareit.user.userStorage.UserRepository;
import ru.practicum.shareit.util.BookingState;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImlp implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    public BookingServiceImlp(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @SneakyThrows
    public BookingDto createBooking(Long userId, BookingDto bookingDto) throws ValidationException, AvailableException {
        validateBooking(bookingDto);
        if(!userRepository.existsById(userId)) throw new WrongIdException("Пользователь несуществует");
        Booking booking = BookingMapper.toBooking(bookingDto);
        if(!itemRepository.existsById(booking.getItemId())) throw new NotFoundException("Вещь не найдена");
        if(!itemRepository.getReferenceById(booking.getItemId()).getIsAvailable())
            throw new AvailableException("Вещь недоступная для бронирования");
        booking.setBookerId(userId);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) throws AccessException {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (itemRepository.getReferenceById(
                bookingRepository.getReferenceById(
                        bookingId).getItemId()).getOwnerId() != userId)
            throw new AccessException("Пользователь не является хозяином вещи");
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else booking.setStatus(Status.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingFromId(Long userId, Long bookingId) throws AccessException {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Long ownerId = itemRepository.getReferenceById(booking.getItemId()).getOwnerId();
        if(booking.getBookerId() != userId || userId != ownerId)
            throw new AccessException("Пользователь не является арендатором или хозяином вещи");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookingsWithStatus(Long userId, String state) throws ValidationException {
        List<Booking> bookings = null;
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now()
                );
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now()
                );
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterAndEndAfterOrderByStartDesc(
                        userId, LocalDateTime.now(), LocalDateTime.now()
                );
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, Status.WAITING
                );
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId, Status.REJECTED
                );
                break;
            default:
                throw new ValidationException("Неверные параметры");
        }
        List<BookingDto> bookingsDto = new ArrayList<>();
        for(Booking booking: bookings) {
            bookingsDto.add(BookingMapper.toBookingDto(booking));
        }
        return bookingsDto;
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, String state) {
        return null;
    }

    private void validateBooking(BookingDto bookingDto) throws ValidationException {
        if (bookingDto == null) throw new ValidationException("Бронирование не передано");
        if (bookingDto.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException("Дата старта в прошлом");
        if (bookingDto.getEnd().isBefore(LocalDateTime.now()))
            throw new ValidationException(("Дата окончания в прошлом"));
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()))
            throw new ValidationException("Дата окончания раньше даты начала");
    }
}
