package ru.practicum.shareit.item.model;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

/**
 * // TODO .
 */
@Component
@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private String request;
}
