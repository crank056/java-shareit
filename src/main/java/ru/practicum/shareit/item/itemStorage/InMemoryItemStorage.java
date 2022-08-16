package ru.practicum.shareit.item.itemStorage;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NullItemFieldException;
import ru.practicum.shareit.exceptions.WrongIdException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private List<Item> items = new ArrayList<>();
    private static long lastUsedId = 1;

    @SneakyThrows
    public Item addItem(Item item) {
        isValid(item);
        item.setId(getNextId());
        log.info("Получен объект item в хранилище, объект: {}", item);
        items.add(item);
        return getItemFromId(item.getId());
    }

    public Item refreshItem(Item item, Long id) throws WrongIdException {
        log.info("Получен объект item в хранилище, объект: {}", item);
        Item refreshingItem = getItemFromId(id);
        log.info("Размер хранилища вещей до обновления: {}", items.size());
        if (item.getName() != null) refreshingItem.setName(item.getName());
        if (item.getIsAvailable() != null) refreshingItem.setIsAvailable(item.getIsAvailable());
        if (item.getDescription() != null) refreshingItem.setDescription(item.getDescription());
        if (item.getOwner() != null) refreshingItem.setOwner(item.getOwner());
        if (item.getRequest() != null) refreshingItem.setRequest(item.getRequest());
        log.info("Размер хранилища вещей после обновления: {}", items.size());
        return getItemFromId(id);
    }

    public Item getItemFromId(Long id) throws WrongIdException {
        Item item = null;
        for (Item findItem : items) {
            if (findItem.getId() == id) {
                item = findItem;
            }
        }
        if (item == null) throw new WrongIdException("Вещь с таким id отсутствует");
        return item;
    }

    public List<Item> getAllItemsFromUserId(Long id) {
        List<Item> userItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId().equals(id)) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    public List<Item> getAllItems() {
        return items;
    }

    private long getNextId() {
        return lastUsedId++;
    }

    private void isValid(Item item) throws NullItemFieldException {
        if (item.getName().isBlank() || item.getName().isEmpty() ||
                item.getIsAvailable() == null ||
                item.getDescription() == null || item.getDescription().isBlank())
            throw new NullItemFieldException("Пустое или отсуствующее имя");
    }
}
