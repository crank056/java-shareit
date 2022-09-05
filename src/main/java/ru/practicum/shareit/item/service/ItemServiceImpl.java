package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.itemRepository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.userStorage.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    public Item addItem(ItemDto itemDto, Long userId) throws WrongIdException, ValidationException {
        validateItem(itemDto);
        Item item = ItemMapper.toItem(itemDto);
        if (userId == null) throw new WrongIdException("Не передан id пользователя");
        if (!userRepository.existsById(userId)) throw new WrongIdException("Нет такого пользователя");
        item.setOwner(userRepository.getReferenceById(userId));
        log.info("Получен объект item в сервисе, объект: {}", item);
        return itemRepository.save(item);
    }

    public Item refreshItem(ItemDto itemDto, Long id, Long userId) throws WrongIdException, ValidationException {
        Item item = itemRepository.getReferenceById(id);
        if (!itemRepository.getReferenceById(id).getOwner().getId().equals(userId))
            throw new WrongIdException("Неверный id хозяина вещи");
        log.info("Получен объект item в сервисе, объект: {}", item);
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setIsAvailable(itemDto.getAvailable());
        if (itemDto.getOwner() != null) item.setOwner(itemDto.getOwner());
        if (itemDto.getRequest() != null) item.setRequest(itemDto.getRequest());
        validateItem(ItemMapper.toItemDto(item));
        return itemRepository.save(item);
    }

    public Item getItemFromId(Long id) throws WrongIdException {
        if (!itemRepository.existsById(id)) throw new WrongIdException("Неверный id или вещи несуществует");
        return itemRepository.getReferenceById(id);
    }

    public List<ItemBookingDto> getAllItemsFromUserId(Long id) throws WrongIdException {
        if (userRepository.getReferenceById(id) == null) throw new WrongIdException("Пользователь не существует");
        List<ItemBookingDto> userItemsDto = new ArrayList<>();
        for (Item item : itemRepository.findAllByOwnerOrderByIdAsc(userRepository.getReferenceById(id))) {
            List<Booking> bookings = bookingRepository.findAllByItemOrderByStartDesc(item);
            Booking last = null;
            Booking next = null;
            if(bookings )
                userItemsDto.add(ItemMapper.toItemBookingDto(item, bookings.get(0), bookings.get(1)));
            }
        return userItemsDto;
    }

    public List<ItemDto> getItemsFromKeyWord(String text) {
        List<Item> items = itemRepository.findAll();
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : items) {
            if (item.getDescription().toLowerCase().contains(text.toLowerCase()) && !text.isBlank()) {
                if (item.getIsAvailable()) {
                    itemsDto.add(ItemMapper.toItemDto(item));
                }
            }
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
}
