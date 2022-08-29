package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;
    @Column(nullable = false)
    private Long ownerId;
    @ElementCollection
    @CollectionTable(name="users", joinColumns=@JoinColumn(name="id"))
    @Column(nullable = false)
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
