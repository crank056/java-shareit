package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemDtoValid {
    @NotBlank(message = "Нет имени")
    private String name;
    @NotBlank(message = "Нет описания")
    @Size(max = 200, message = "максимальный размер 200 символов")
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}