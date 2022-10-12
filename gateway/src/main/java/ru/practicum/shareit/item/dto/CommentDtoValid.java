package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CommentDtoValid {
    @NotBlank(message = "Нет комментария")
    private String text;
}
