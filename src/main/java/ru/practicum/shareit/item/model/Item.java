package ru.practicum.shareit.item.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * // TODO .
 */
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private boolean isAvailable;
    private User owner;
    private ItemRequest request;

    public Item(Long id, String name, String description, boolean isAvailable, User owner, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.owner = owner;
        this.request = request;
    }
}
