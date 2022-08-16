package ru.practicum.shareit.item.itemStorage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemStorage {
    Item addItem(Item item);

    Item refreshItem(Item item, Long id) throws WrongIdException;

    Item getItemFromId(Long id) throws WrongIdException;

    List<Item> getAllItemsFromUserId(Long id);

    List<Item> getAllItems();
}
