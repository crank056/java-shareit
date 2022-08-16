package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.itemStorage.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.userStorage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private ItemStorage itemStorage;
    private UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Item addItem(ItemDto itemDto, Long userId) throws WrongIdException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userStorage.getUserFromId(userId));
        log.info("Получен объект item в сервисе, объект: {}", item);
        return itemStorage.addItem(item);
    }

    public Item refreshItem(ItemDto itemDto, Long id, Long userId) throws WrongIdException {
        Item item = ItemMapper.toItem(itemDto);
        if (!itemStorage.getItemFromId(id).getOwner().getId().equals(userId))
            throw new WrongIdException("Неверный id хозяина вещи");
        log.info("Получен объект item в сервисе, объект: {}", item);
        return itemStorage.refreshItem(item, id);
    }

    public Item getItemFromId(Long id) throws WrongIdException {
        return itemStorage.getItemFromId(id);
    }

    public List<ItemDto> getAllItemsFromUserId(Long id) throws WrongIdException {
        if (userStorage.getUserFromId(id) == null) throw new WrongIdException("Пользователь не существует");
        List<ItemDto> userItemsDto = new ArrayList<>();
        for (Item item : itemStorage.getAllItemsFromUserId(id)) {
            userItemsDto.add(ItemMapper.toItemDto(item));
        }
        return userItemsDto;
    }

    public List<ItemDto> getItemsFromKeyWord(String text) {
        List<Item> items = itemStorage.getAllItems();
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
