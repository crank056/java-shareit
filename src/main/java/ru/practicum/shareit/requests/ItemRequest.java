package ru.practicum.shareit.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
public class ItemRequest {
    @Id
    private Long id;
    @Column
    private String description;
    @Column(name = "user_id")
    private Long requesterId;
    @Column
    private LocalDateTime created;
}
