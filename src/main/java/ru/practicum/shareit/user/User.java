package ru.practicum.shareit.user;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;

/**
 *
 * // TODO .
 */
@Data
public class User {
    private Long id;
    private String name;
    @Email
    private String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
