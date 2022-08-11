package ru.practicum.shareit.requests.dto;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
@Component
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}
