package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.AccessException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.Repository.CommentRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.Repository.ItemRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.user.userStorage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public ItemServiceImpl(
            ItemRepository itemRepository,
            UserRepository userRepository,
            BookingRepository bookingRepository,
            CommentRepository commentRepository, RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.requestRepository = requestRepository;
    }

    public ItemDto addItem(ItemDto itemDto, Long userId) throws WrongIdException, ValidationException {
        validateItem(itemDto);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.getReferenceById(itemDto.getRequestId());
        }
        Item item = ItemMapper.toItem(itemDto, userRepository.getReferenceById(userId), request);
        if (userId == null) throw new WrongIdException("Не передан id пользователя");
        if (!userRepository.existsById(userId)) throw new WrongIdException("Нет такого пользователя");
        item.setOwner(userRepository.getReferenceById(userId));
        log.info("Получен объект item в сервисе, объект: {}", item);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto refreshItem(ItemDto itemDto, Long id, Long userId)
            throws WrongIdException, ValidationException {
        if (!itemRepository.existsById(id)) throw new WrongIdException("Нет такой вещи");
        Item item = itemRepository.getReferenceById(id);
        if (!itemRepository.getReferenceById(id).getOwner().getId().equals(userId))
            throw new WrongIdException("Неверный id хозяина вещи");
        log.info("Получен объект item в сервисе, объект: {}", item);
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setIsAvailable(itemDto.getAvailable());
        if (itemDto.getOwnerId() != null) item.setOwner(userRepository.getReferenceById(userId));
        if (itemDto.getRequestId() != null) item.setRequest(requestRepository.getReferenceById(itemDto.getId()));
        validateItem(ItemMapper.toItemDto(item));
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemBookingDto getItemFromId(Long userId, Long id) throws WrongIdException {
        if (!itemRepository.existsById(id)) throw new WrongIdException("Неверный id или вещи несуществует");
        Item item = itemRepository.getReferenceById(id);
        List<Booking> bookings = bookingRepository.findAllByItem(item);
        BookingItemDto last = null;
        BookingItemDto next = null;
        List<Comment> comments = commentRepository.findAllByItemOrderByCreatedAsc(item);
        List<CommentDto> commentsDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentsDto.add(CommentMapper.toCommentDto(comment));
        }
        if (bookings.size() >= 1) last = BookingMapper.toBookingItemDto(bookings.get(0));
        if (bookings.size() >= 2) next = BookingMapper.toBookingItemDto(bookings.get(1));
        ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(item, last, next, commentsDto);
        if (!item.getOwner().getId().equals(userId))
            itemBookingDto = ItemMapper.toItemBookingDto(item, null, null, commentsDto);
        return itemBookingDto;
    }

    public List<ItemBookingDto> getAllItemsFromUserId(Long id, int from, int size)
        throws WrongIdException, ValidationException {
        if (!userRepository.existsById(id)) throw new WrongIdException("Пользователь не существует");
        List<ItemBookingDto> userItemsDto = new ArrayList<>();
        validatePageSize(from, size);
        Pageable page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        for (Item item : itemRepository.findAllByOwnerOrderByIdAsc(userRepository.getReferenceById(id), page)) {
            List<Comment> comments = commentRepository.findAllByItemOrderByCreatedAsc(item);
            List<CommentDto> commentsDto = new ArrayList<>();
            for (Comment comment : comments) {
                commentsDto.add(CommentMapper.toCommentDto(comment));
            }
            List<Booking> bookings = bookingRepository.findAllByItem(item);
            BookingItemDto last = null;
            BookingItemDto next = null;
            if (bookings.size() >= 1) last = BookingMapper.toBookingItemDto(bookings.get(0));
            if (bookings.size() >= 2) next = BookingMapper.toBookingItemDto(bookings.get(1));
            userItemsDto.add(ItemMapper.toItemBookingDto(item, last, next, commentsDto));
        }
        return userItemsDto;
    }

    public List<ItemDto> getItemsFromKeyWord(String text, int from, int size) throws ValidationException {
        validatePageSize(from, size);
        Pageable page = PageRequest.of(from / size, size, Sort.by("id").ascending());
        if (text.isEmpty() && text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findFromKeyWord(text, page);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    private void validateItem(ItemDto itemDto) throws ValidationException {
        if (itemDto.getAvailable() == null) throw new ValidationException("Нет информации о доступности вещи");
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getName().isBlank())
            throw new ValidationException("Имя отсутствует");
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()
                || itemDto.getDescription().isEmpty()) throw new ValidationException("Отстутствует описание");
    }

    private void validatePageSize(int from, int size) throws ValidationException {
        if (from < 0 || size < 1) throw new ValidationException("Неверные значения формата");
    }

    public CommentDto addComment(Comment comment, Long itemId, Long userId)
            throws AccessException, ValidationException {
        if (comment.getText().isBlank() || comment.getText().isEmpty())
            throw new ValidationException("Комментарий не может быть пустым");
        List<Booking> bookings = bookingRepository.findAllByBookerAndEndBeforeOrderByStartDesc(
                userRepository.getReferenceById(userId), LocalDateTime.now());
        Boolean booker = false;
        for (Booking booking: bookings) {
            if (booking.getItem().getId().equals(itemId)) booker = true;
        }
        if (!booker) throw new AccessException("Вы не брали вещь в аренду");
        comment.setItem(itemRepository.getReferenceById(itemId));
        comment.setUser(userRepository.getReferenceById(userId));
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
