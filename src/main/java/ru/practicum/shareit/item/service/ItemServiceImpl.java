package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.WrongIdException;
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

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public Item addItem(ItemDto itemDto, Long userId) throws WrongIdException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userRepository.getReferenceById(userId).getId());
        log.info("Получен объект item в сервисе, объект: {}", item);
        return itemRepository.save(item);
    }

    public Item refreshItem(ItemDto itemDto, Long id, Long userId) throws WrongIdException {
        Item item = ItemMapper.toItem(itemDto);
        if (!itemRepository.getReferenceById(id).getOwnerId().equals(userId))
            throw new WrongIdException("Неверный id хозяина вещи");
        log.info("Получен объект item в сервисе, объект: {}", item);
        return itemRepository.save(item);
    }

    public Item getItemFromId(Long id) throws WrongIdException {
        return itemRepository.getReferenceById(id);
    }

    public List<ItemDto> getAllItemsFromUserId(Long id) throws WrongIdException {
        if (userRepository.getReferenceById(id) == null) throw new WrongIdException("Пользователь не существует");
        List<ItemDto> userItemsDto = new ArrayList<>();
        for (Item item : itemRepository.findAll()) {
            if (item.getOwnerId() == id) {
                userItemsDto.add(ItemMapper.toItemDto(item));
            }
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
}
