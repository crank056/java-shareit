package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDtoValid {
    @NotBlank(message = "Нет имени")
    private String name;
    @NotBlank(message = "Нет почты")
    @Email(regexp = "\\w+@\\w+\\.(ru|com)",
            message = "Невалидный емайл")
    private String email;
}
