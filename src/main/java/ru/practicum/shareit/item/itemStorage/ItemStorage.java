package ru.practicum.shareit.item.itemStorage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.model.Item;
@Repository
public interface ItemStorage {
    public Item addItem(Item item) throws WrongIdException;
}
