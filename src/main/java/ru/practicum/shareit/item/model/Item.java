package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "is_available")
    private Boolean isAvailable;
    private Long ownerId;
    private Long requestId;

    public Item(Long id, String name, String description, Boolean isAvailable, Long ownerId, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.ownerId = ownerId;
        this.requestId = requestId;
    }
}
