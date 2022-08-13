package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * // TODO .
 */
@Data
public class User {
    private Long id;
    private String name;
    @NotBlank
    @NotNull
    private String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
