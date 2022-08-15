package ru.practicum.shareit.item.itemStorage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemStorage {
    public Item addItem(Item item) throws WrongIdException;

    public Item refreshItem(Item item, Long id) throws WrongIdException;

    public Item getItemFromId(Long id) throws WrongIdException;

    public List<Item> getAllItemsFromUserId(Long id);
}
