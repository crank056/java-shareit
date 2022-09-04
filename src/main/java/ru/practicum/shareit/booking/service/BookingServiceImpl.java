package ru.practicum.shareit.booking.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Not;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.itemRepository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserRepository;
import ru.practicum.shareit.util.BookingState;
import ru.practicum.shareit.util.Status;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @SneakyThrows
    @Transactional
    public BookingDto createBooking(Long userId, BookingItemDto bookingItemDto) throws ValidationException, AvailableException {
        validateBooking(bookingItemDto);
        if(!userRepository.existsById(userId)) throw new WrongIdException("Пользователь несуществует");
        if(!itemRepository.existsById(bookingItemDto.getItemId())) throw new NotFoundException("Вещь не найдена");
        if(!itemRepository.getReferenceById(bookingItemDto.getItemId()).getIsAvailable())
            throw new AvailableException("Вещь недоступная для бронирования");
        if(itemRepository.getReferenceById(bookingItemDto.getItemId()).getOwner().getId().equals(userId))
            throw new AccessException("Нельзя забронировать вещь принадлежащую вам");
        Booking booking = BookingMapper.buildBookingFromItemDto(
                bookingItemDto,
                itemRepository.getReferenceById(bookingItemDto.getItemId()),
                userRepository.getReferenceById(userId)
        );
        booking.setStatus(Status.WAITING);
        log.info("Объект: {}", booking);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved)
            throws AccessException, ValidationException {
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if (itemRepository.getReferenceById(
                bookingRepository.getReferenceById(
                        bookingId).getItem().getId()).getOwner().getId() != userId)
            throw new AccessException("Пользователь не является хозяином вещи");
        if (approved) {
            if(booking.getStatus().equals(Status.APPROVED)) throw new ValidationException("Уже подтверждено");
            booking.setStatus(Status.APPROVED);
        } else booking.setStatus(Status.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingFromId(Long userId, Long bookingId) throws AccessException, NotFoundException {
        if(!bookingRepository.existsById(bookingId)) throw new NotFoundException("Бронь отсутствует");
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Long ownerId = itemRepository.getReferenceById(booking.getItem().getId()).getOwner().getId();
        if(booking.getBooker().getId() != userId) {
            if(ownerId != userId) {
                throw new AccessException("Пользователь не является арендатором или хозяином вещи");
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsFromUserId(Long userId, String state) throws ValidationException, WrongIdException {
        List<Booking> bookings = null;
        if(!userRepository.existsById(userId)) throw new WrongIdException("Пользователя несуществует");
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
    public List<BookingDto> getBookingsFromOwnerId(Long userId, String state) throws WrongIdException, ValidationException {
        List<Booking> bookings = null;
        User owner = userRepository.getReferenceById(userId);
        if(!userRepository.existsById(userId)) throw new WrongIdException("Пользователя несуществует");
        switch (BookingState.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByOwner(owner);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                        owner, LocalDateTime.now(), LocalDateTime.now()
                );
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerAndEndBeforeOrderByStartDesc(
                        owner, LocalDateTime.now()
                );
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerAndStartAfterAndEndAfterOrderByStartDesc(
                        owner, LocalDateTime.now(), LocalDateTime.now()
                );
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        owner, Status.WAITING
                );
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerAndStatusOrderByStartDesc(
                        owner, Status.REJECTED
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

    private void validateBooking(BookingItemDto bookingItemDto) throws ValidationException {
        if (bookingItemDto == null) throw new ValidationException("Бронирование не передано");
        if (bookingItemDto.getStart().isBefore(LocalDateTime.now()))
            throw new ValidationException("Дата старта в прошлом");
        if (bookingItemDto.getEnd().isBefore(LocalDateTime.now()))
            throw new ValidationException(("Дата окончания в прошлом"));
        if (bookingItemDto.getEnd().isBefore(bookingItemDto.getStart()))
            throw new ValidationException("Дата окончания раньше даты начала");
    }
}
