package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {
    public Item addItem(ItemDto itemDto, Long userId) throws WrongIdException;

    public Item refreshItem(ItemDto itemDto, Long id, Long userId) throws WrongIdException;

    public Item getItemFromId(Long id) throws WrongIdException;

    public List<ItemDto> getAllItemsFromUserId(Long id) throws WrongIdException;

    public List<ItemDto> getItemsFromKeyWord(String text);


}
