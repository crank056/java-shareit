package ru.practicum.shareit.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RequestDtoValid {
    @NotBlank(message = "Нет описания")
    @Size(max = 200, message = "Маск длина 200 символов")
    private String description;
}