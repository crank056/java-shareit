package ru.practicum.shareit.booking.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.Repository.ItemRepository;
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

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingItemDto bookingItemDto)
            throws ValidationException, WrongIdException, AvailableException, AccessException, NotFoundException {
        validateBooking(bookingItemDto);
        if (!userRepository.existsById(userId)) throw new WrongIdException("Пользователь несуществует");
        if (!itemRepository.existsById(bookingItemDto.getItemId())) throw new NotFoundException("Вещь не найдена");
        if (!itemRepository.getReferenceById(bookingItemDto.getItemId()).getIsAvailable())
            throw new AvailableException("Статус вещи - недоступен для бронирования");
        if (itemRepository.getReferenceById(bookingItemDto.getItemId()).getOwner().getId().equals(userId))
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
            throws AccessException, ValidationException, WrongIdException {
        if(!bookingRepository.existsById(bookingId)) throw new WrongIdException("Бронирования не существует!");
        Booking booking = bookingRepository.getReferenceById(bookingId);
        if(!userRepository.existsById(userId)) throw new WrongIdException("Пользователя не существует");
        if (!itemRepository.getReferenceById(
                bookingRepository.getReferenceById(
                        bookingId).getItem().getId()).getOwner().getId().equals(userId))
            throw new AccessException("Пользователь не является хозяином вещи");
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) throw new ValidationException("Уже подтверждено");
            booking.setStatus(Status.APPROVED);
        } else booking.setStatus(Status.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingFromId(Long userId, Long bookingId) throws AccessException, NotFoundException, WrongIdException {
        if(!userRepository.existsById(userId)) throw new WrongIdException("Нет такого пользователя");
        if (!bookingRepository.existsById(bookingId)) throw new NotFoundException("Бронь отсутствует");
        Booking booking = bookingRepository.getReferenceById(bookingId);
        Long ownerId = itemRepository.getReferenceById(booking.getItem().getId()).getOwner().getId();
        if (!booking.getBooker().getId().equals(userId)) {
            if (!ownerId.equals(userId)) {
                throw new AccessException("Пользователь не является арендатором или хозяином вещи");
            }
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsFromUserId(Long userId, String state, int from, int size)
            throws ValidationException, WrongIdException {
        List<Booking> bookings = new ArrayList<>();
        if (from < 0 || size < 1) throw new ValidationException("Неверные значения формата");
        Pageable page;
        if (!userRepository.existsById(userId)) throw new WrongIdException("Пользователя несуществует");
        switch (BookingState.valueOf(state)) {
            case ALL:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByBookerId(userId, page);
                break;
            case CURRENT:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByBookerIdInCurrent(
                        userId, LocalDateTime.now(), LocalDateTime.now(), page
                );
                break;
            case PAST:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByBookerIdInPastWithPage(
                        userId, LocalDateTime.now(), page
                );
                break;
            case FUTURE:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByBookerIdInFuture(
                        userId, LocalDateTime.now(), LocalDateTime.now(), page
                );
                break;
            case WAITING:
                page = PageRequest.of(from / size, size, Sort.by("start").ascending());
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        userId, Status.WAITING, page
                );
                break;
            case REJECTED:
                page = PageRequest.of(from / size, size, Sort.by("start").ascending());
                bookings = bookingRepository.findAllByBookerIdAndStatus(
                        userId, Status.REJECTED, page
                );
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", BookingState.valueOf(state)));
        }
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingsDto.add(BookingMapper.toBookingDto(booking));
        }
        return bookingsDto;
    }

    @Override
    public List<BookingDto> getBookingsFromOwnerId(Long userId, String state, int from, int size)
            throws WrongIdException, ValidationException {
        List<Booking> bookings = new ArrayList<>();
        if (from < 0 || size < 1) throw new ValidationException("Неверные значения формата");
        Pageable page;
        User owner = userRepository.getReferenceById(userId);
        if (!userRepository.existsById(userId)) throw new WrongIdException("Пользователя несуществует");
        switch (BookingState.valueOf(state)) {
            case ALL:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByOwner(owner, page);
                break;
            case CURRENT:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByItemOwnerInCurrent(
                        owner, LocalDateTime.now(), LocalDateTime.now(), page
                );
                break;
            case PAST:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByItemOwnerInPastWithPage(
                        owner, LocalDateTime.now(), page
                );
                break;
            case FUTURE:
                page = PageRequest.of(from / size, size, Sort.by("start").descending());
                bookings = bookingRepository.findAllByItemOwnerInFuture(
                        owner, LocalDateTime.now(), LocalDateTime.now(), page
                );
                break;
            case WAITING:
                page = PageRequest.of(from / size, size, Sort.by("start").ascending());
                bookings = bookingRepository.findAllByItemOwnerAndStatus(
                        owner, Status.WAITING, page
                );
                break;
            case REJECTED:
                page = PageRequest.of(from / size, size, Sort.by("start").ascending());
                bookings = bookingRepository.findAllByItemOwnerAndStatus(
                        owner, Status.REJECTED, page
                );
                break;
            default:
                throw new ValidationException(String.format("Unknown state: %s", BookingState.valueOf(state)));
        }
        List<BookingDto> bookingsDto = new ArrayList<>();
        for (Booking booking : bookings) {
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
