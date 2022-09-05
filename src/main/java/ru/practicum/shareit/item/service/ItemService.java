package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public interface ItemService {
    Item addItem(ItemDto itemDto, Long userId) throws WrongIdException, ValidationException;

    Item refreshItem(ItemDto itemDto, Long id, Long userId) throws WrongIdException, ValidationException;

    Item getItemFromId(Long id) throws WrongIdException;

    List<ItemBookingDto> getAllItemsFromUserId(Long id) throws WrongIdException;

    List<ItemDto> getItemsFromKeyWord(String text);


}
