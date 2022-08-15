package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
@Service
public interface ItemService {
    public Item addItem(ItemDto itemDto, Long id) throws WrongIdException;
}
