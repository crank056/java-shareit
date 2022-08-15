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

@Service
@Slf4j
public class ItemServiceImpl implements ItemService{
    private ItemStorage itemStorage;
    private UserStorage userStorage;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    public Item addItem(ItemDto itemDto, Long id) throws WrongIdException {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userStorage.getUserFromId(id));
        log.info("Получен объект item в сервисе, объект: {}", item);
        return itemStorage.addItem(ItemMapper.toItem(itemDto));
    }
}
